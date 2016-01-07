package com.akash;

import com.cards.UrlToMetaData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("xyz/")
@Produces("application/json")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @POST
    @Path("new/")
    public UrlToMetaData.NewRequest queryNewCard(@FormParam("data") final String data) {
        UrlToMetaData.NewRequest n = new UrlToMetaData.NewRequest();
        n.setTitle("Howdy");
        n.setDescription(data);
        return n;
    }
}
