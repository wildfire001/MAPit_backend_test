package com.mapit.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Text;
import com.mapit.backend.Properties_and_Values.Commands;
import com.mapit.backend.Properties_and_Values.DatastoreKindNames;
import com.mapit.backend.Properties_and_Values.DatastorePropertyNames;

import java.util.ArrayList;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "userinfoModelApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.mapit.com",
                ownerName = "backend.mapit.com",
                packagePath = ""
        )
)
public class UserinfoEndpoint {
    public ResponseMessages response_messages;

    @ApiMethod(name = "setUserInfo", path = "setUserInfopath", httpMethod = ApiMethod.HttpMethod.POST)
    public ResponseMessages setUserInfo(UserinfoModel userinformation, @Named("RequestType") String Request_Type) throws EntityNotFoundException{
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity Userinfo_Kind = new Entity(DatastoreKindNames.Userinfo.getKind());
        Userinfo_Kind.setProperty(DatastorePropertyNames.Userinfo_Username.getProperty(), userinformation.getName());
        Userinfo_Kind.setProperty(DatastorePropertyNames.Userinfo_Mail.getProperty(), userinformation.getMail());
        Userinfo_Kind.setUnindexedProperty(DatastorePropertyNames.Userinfo_Password.getProperty(), userinformation.getPassword());
        Userinfo_Kind.setUnindexedProperty(DatastorePropertyNames.Userinfo_latitude.getProperty(), userinformation.getLatitude());
        Userinfo_Kind.setUnindexedProperty(DatastorePropertyNames.Userinfo_longitude.getProperty(), userinformation.getLongitude());
        Userinfo_Kind.setUnindexedProperty(DatastorePropertyNames.Userinfo_location.getProperty(), userinformation.getLocation());

        if(userinformation.getMobilephone() != null)
        {
            Userinfo_Kind.setUnindexedProperty(DatastorePropertyNames.Userinfo_Mobile.getProperty(), userinformation.getMobilephone());
        }
        else
        {
            Userinfo_Kind.setUnindexedProperty(DatastorePropertyNames.Userinfo_Mobile.getProperty(), "");
        }

        if(userinformation.getImagedata() != null)
        {
            Text image_Data = new Text(userinformation.getImagedata());
            Userinfo_Kind.setUnindexedProperty(DatastorePropertyNames.Userinfo_Profilepic.getProperty(), image_Data);
        }
        else
        {
            Text image_Data = new Text("");
            Userinfo_Kind.setUnindexedProperty(DatastorePropertyNames.Userinfo_Profilepic.getProperty(), image_Data);
        }


        if(Request_Type.equals(Commands.Userinfo_create.getCommand())) {

            UserinfoModel mailquery = new UserinfoModel();
            mailquery.setMail(userinformation.getMail());

            ArrayList<UserinfoModel> checkMail = getUserinfo(mailquery, Commands.Userinfo_getpass.getCommand());

            if (checkMail.size() > 0) {
                response_messages = new ResponseMessages();
                response_messages.setMessage(response_messages.Userinfo_creation_duplicate);
            } else {
                datastore.put(Userinfo_Kind);
                response_messages = new ResponseMessages();
                response_messages.setMessage(response_messages.Userinfo_creation_OK);
            }
        }
        else if(Request_Type.equals(Commands.Userinfo_update.getCommand()))
        {
            Key k = getKeyfromMail(userinformation.getMail());

            Entity updatedinfo = datastore.get(k);

            if(userinformation.getName() != null && userinformation.getName().length() > 0)
                updatedinfo.setProperty(DatastorePropertyNames.Userinfo_Username.getProperty(), userinformation.getName());

            if(userinformation.getPassword() != null && userinformation.getPassword().length() > 0)
                updatedinfo.setUnindexedProperty(DatastorePropertyNames.Userinfo_Password.getProperty(), userinformation.getPassword());

            if(userinformation.getLatitude() != null && userinformation.getLatitude().length() > 0)
                updatedinfo.setUnindexedProperty(DatastorePropertyNames.Userinfo_latitude.getProperty(), userinformation.getLatitude());

            if(userinformation.getLongitude() != null && userinformation.getLongitude().length() > 0)
                updatedinfo.setUnindexedProperty(DatastorePropertyNames.Userinfo_longitude.getProperty(), userinformation.getLongitude());

            if(userinformation.getImagedata() != null && userinformation.getImagedata().length() > 0)
            {
                Text image_Data = new Text(userinformation.getImagedata());
                updatedinfo.setProperty(DatastorePropertyNames.Userinfo_Profilepic.getProperty(), image_Data);
            }

            if(userinformation.getMobilephone() != null && userinformation.getMobilephone().length() > 0)
            {
                updatedinfo.setUnindexedProperty(DatastorePropertyNames.Userinfo_Mobile.getProperty(), userinformation.getMobilephone());
            }

            if(userinformation.getLocation() != null && userinformation.getLocation().length() > 0)
            {
                updatedinfo.setUnindexedProperty(DatastorePropertyNames.Userinfo_location.getProperty(), userinformation.getLocation());
            }

            response_messages = new ResponseMessages();
            response_messages.setMessage(response_messages.Userinfo_update_OK);
            datastore.delete(k);
            datastore.put(updatedinfo);
        }
        return response_messages;
    }


    @ApiMethod(name = "getUserinfo", path = "getUserinfoPath", httpMethod = ApiMethod.HttpMethod.POST)
    public ArrayList<UserinfoModel> getUserinfo(UserinfoModel userinformation, @Named("QueryType") String Query_type) {

        ArrayList <UserinfoModel> Userinfo_Result = new ArrayList<>();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query Userinfo_Query = new Query();

        if(userinformation.getMail() != null)
        {
            Filter Check_Mail_Filter = new Query.FilterPredicate(DatastorePropertyNames.Userinfo_Mail.getProperty(), Query.FilterOperator.EQUAL, userinformation.getMail());
            Userinfo_Query = new Query(DatastoreKindNames.Userinfo.getKind()).setFilter(Check_Mail_Filter);
        }
        else if(userinformation.getName() != null)
        {
            Filter Name_Filter = new Query.FilterPredicate(DatastorePropertyNames.Userinfo_Username.getProperty(), Query.FilterOperator.EQUAL, userinformation.getName());
            Userinfo_Query = new Query(DatastoreKindNames.Userinfo.getKind()).setFilter(Name_Filter);
        }

        PreparedQuery queryResult = datastore.prepare(Userinfo_Query);

        if(Query_type.equals(Commands.Userinfo_getmail.getCommand()))
        {
            for (Entity result : queryResult.asIterable()) {
                UserinfoModel um = new UserinfoModel();

                um.setMail((String) result.getProperty(DatastorePropertyNames.Userinfo_Mail.getProperty()));

                Userinfo_Result.add(um);
            }
        }
        else if(Query_type.equals(Commands.Userinfo_getpass.getCommand()))
        {
            for (Entity result : queryResult.asIterable()) {
                UserinfoModel um = new UserinfoModel();

                um.setMail((String) result.getProperty(DatastorePropertyNames.Userinfo_Mail.getProperty()));
                um.setPassword((String) result.getProperty(DatastorePropertyNames.Userinfo_Password.getProperty()));

                Userinfo_Result.add(um);
            }
        }
        else if(Query_type.equals(Commands.Userinfo_getinfo.getCommand()))
        {
            for (Entity result : queryResult.asIterable()) {
                UserinfoModel um = new UserinfoModel();

                um.setName((String) result.getProperty(DatastorePropertyNames.Userinfo_Username.getProperty()));
                um.setPassword((String) result.getProperty(DatastorePropertyNames.Userinfo_Password.getProperty()));
                um.setMail((String) result.getProperty(DatastorePropertyNames.Userinfo_Mail.getProperty()));


                Text imageText = (Text) result.getProperty(DatastorePropertyNames.Userinfo_Profilepic.getProperty());
                String ImageData = imageText.getValue();
                if(ImageData.length() > 0)
                    um.setImagedata(ImageData);


                String MobilePhone = (String) result.getProperty(DatastorePropertyNames.Userinfo_Mobile.getProperty());
                if(MobilePhone.length() > 0)
                    um.setMobilephone(MobilePhone);


                um.setLatitude((String) result.getProperty(DatastorePropertyNames.Userinfo_latitude.getProperty()));
                um.setLongitude((String) result.getProperty(DatastorePropertyNames.Userinfo_longitude.getProperty()));
                um.setLocation((String) result.getProperty(DatastorePropertyNames.Userinfo_location.getProperty()));

                Userinfo_Result.add(um);
            }
        }

        return Userinfo_Result;

    }

    @ApiMethod(name = "getKeyfromMail", path = "getKeyfromMailPath", httpMethod = ApiMethod.HttpMethod.POST)
    public Key getKeyfromMail(@Named("Mail") String Mail)
    {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query Userinfo_Query = new Query();
        Filter Mail_Filter = new Query.FilterPredicate(DatastorePropertyNames.Userinfo_Mail.getProperty(), Query.FilterOperator.EQUAL, Mail);
        Userinfo_Query = new Query(DatastoreKindNames.Userinfo.getKind()).setFilter(Mail_Filter);

        PreparedQuery queryResult = datastore.prepare(Userinfo_Query);

        Key mailKey = null;

        for (Entity result : queryResult.asIterable()) {
            mailKey = result.getKey();
        }

        return mailKey;
    }
}