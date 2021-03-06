/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.apps.googleapps;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.xwiki.component.annotation.Role;
import org.xwiki.stability.Unstable;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

/**
 * The specification of the methods that the manager of the GoogleApps application
 * is doing. Methods of this interface are mostly called by the script-service (itself
 * called by the views).
 *
 * @version $Id$
 * @since 3.0
 */
@Role
public interface GoogleAppsManager
{


    /**
     * @return if the application is licensed and activated
     * @throws XWikiException in case a context cannot be read from thread.
     * @since 3.0
     */
    @Unstable
    boolean isActive() throws XWikiException;

    /**
     *
     * @return if the app is configured to use the Google Drive integration (default: yes).
     * @since 3.0
     */
    @Unstable
    boolean useDrive();


    /**
     * Reads the manifest to find when the JAR file was assembled by maven.
     * @return the build date.
     * @since 3.0
     */
    @Unstable
    Date getBuildTime();


    /** Inspects the stored information to see if an authorization or a redirect needs to be pronounced.
     *
     * @return found credential
     * @throws XWikiException if the interaction with xwiki failed
     * @throws IOException if a communication problem to Google services occured
     * @since 3.0
     */
    @Unstable
    Credential authorize() throws XWikiException, IOException;


    /** Inspects the stored information to see if an authorization or a redirect needs to be pronounced.
     *
     * @param redirect If a redirect can be done
     * @return found credential
     * @throws XWikiException if the interaction with xwiki failed
     * @throws IOException if a communication problem to Google services occured
     * @since 3.0
     */
    @Unstable
    Credential authorize(boolean redirect) throws XWikiException, IOException;

    /**
     * Performs the necessary communication with Google-Services to fetch identity and
     * update the XWiki-user object or possibly sends a redirect to a Google login screen.
     *
     * @return "failed login" if failed, "no user" (can be attempted to Google-OAuth),
     *          or "ok" if successful
     * @since 3.0
     */
    @Unstable
    String updateUser();

    /**
     *  Get the list of all documents in the user's associated account.
     *
     * @return A list of max 10 documents.
     * @throws XWikiException if an authorization process failed.
     * @throws IOException if a communication process to Google services occurred.
     * @since 3.0
     */
    @Unstable
    List<File> getDocumentList() throws XWikiException, IOException;

    /** Fetches a list of Google Drive document matching a substring query in the filename.
     *
     * @param query the expected query (e.g. fullText contains winter ski)
     * @param nbResults max number of results
     * @return The list of files at Google Drive.
     * @throws XWikiException if an XWiki issue occurs
     * @throws IOException if an error interacting with Google services occurred
     * @since 3.0
     */
    @Unstable
    List<File> listDriveDocumentsWithTypes(String query, int nbResults) throws XWikiException, IOException;

    /** Fetches a list of Google Drive document matching a given query.
     *
     * @param query the expected filename substring
     * @param nbResults max number of results
     * @return The list of files at Google Drive.
     * @throws XWikiException if an XWiki issue occurs
     * @throws IOException if an error interacting with Google services occurred
     * @since 3.0
     */
    @Unstable
    FileList listDocuments(String query, int nbResults) throws XWikiException, IOException;

    /**
     * Fetches the google-drive document's representation and stores it as attachment.
     * @param page attach to this page
     * @param name attach using this file name
     * @param id store object attached to this attachment using this id (for later sync)
     * @param url fetch from this URL
     * @return true if successful
     * @throws XWikiException if an issue occurred in XWiki
     * @throws IOException if an issue occurred in the communication with teh Google services
     * @since 3.0
     */
    @Unstable
    boolean retrieveFileFromGoogle(String page, String name, String id, String url) throws XWikiException, IOException;

    /**
     * Extracts metadata about the Google Drive document corresponding to the named attachment.
     *
     * @param pageName The XWiki page where the attachment is
     * @param fileName The filename of the attachment
     * @return information about the corresponding Google Drive document
     * @throws XWikiException if something happened at XWiki side
     * @since 3.0
     */
    @Unstable
    GoogleAppsManager.GoogleDocMetadata getGoogleDocument(String pageName, String fileName) throws XWikiException;

    /**
     * Simple pojo for metadata about a google doc.
     * @since 3.0
     */
    @Unstable
    class GoogleDocMetadata
    {
        /**
         * Google's internal id to find the document again.
         */
        public String id;

        /**
         * URL to direct the user to for editing.
         */
        public String editLink;

        /**
         * URL to pull from in order to fetch the document.
         */
        public String exportLink;
    }

    /**
     * Reads the extension and document name.
     * @param docName the raw docName
     * @param elink the link where to read the extension name
     * @return an array with extension and simplified document name
     * @since 3.0
     */
    @Unstable
    String[] getExportLink(String docName, String elink);


    /**
     * Inserts the current information on the document to be embedded.
     *
     * @param docId the identifier of the Google Docs document to be embedded
     * @param doc the XWiki document where to attach the embedding
     * @param obj the XWiki object where this embedding is to be updated (or null if it is to be created)
     * @param nb the number of the embedding across all the page's embeddings
     * @return the created or actualized document
     * @throws IOException If the communication with Google went wrong
     * @throws XWikiException If something at the XWiki side went wrong (e.g. saving)
     */
    @Unstable
    public BaseObject createOrUpdateEmbedObject(String docId, XWikiDocument doc, BaseObject obj, int nb) throws IOException, XWikiException;

    /**
     * Saves the attachment stored in XWiki to the Google drive of the user attached to the current logged-in user.
     * @param page the XWiki page name
     * @param name the attachment name
     * @return a record with the keys fileName, exportLink, version, editLink,  embedLink,
     *      and google-user's email-address
     * @throws XWikiException if something went wrong at the XWiki side
     * @throws IOException if something went wrong int he communication with Google drive.
     * @since 3.0
     */
    @Unstable
    Map<String, Object> saveAttachmentToGoogle(String page, String name) throws XWikiException, IOException;

    /**
     * Reads the google user-info attached to the current user as stored in the request.
     *
     * @return the google user-info with keys displayName, emails (array of type,value pairs),
     *    etag, id, image (map with keys isDefault and url), kind, language,
     *    name (map with keys familyName and givenName).
     * @since 3.0
     */
    @Unstable
    Map<String, Object> getGoogleUser();

}
