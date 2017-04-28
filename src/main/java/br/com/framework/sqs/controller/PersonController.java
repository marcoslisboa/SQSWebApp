package br.com.framework.sqs.controller;

import br.com.framework.sqs.config.PersonQueue;
import br.com.framework.sqs.dto.PersonDTO;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Marcos Lisboa on 27/04/17.
 */
@Stateless
@Path("person")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class PersonController {

    private static List<PersonDTO> persons = new ArrayList<>();

    @EJB
    private PersonQueue personQueue;

    static {
        persons.add(new PersonDTO(){{
            setName("Marcos");
            setActive(true);
            setCreatedAt(new Date());
            setEmail("marcos.lisboa@mail.com");
        }});
    }

    @POST
    @Path("queue")
    public Response queue(PersonDTO person) {
        personQueue.sendMessage(person);
        persons.add(person);
        return Response.ok(person).build();
    }

    @GET
    public Response list() {
        return Response.ok(persons).build();
    }





}
