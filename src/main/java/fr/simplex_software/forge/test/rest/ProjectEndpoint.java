package fr.simplex_software.forge.test.rest;

import java.util.*;

import javax.ejb.*;
import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.*;

import fr.simplex_software.forge.test.model.*;

/**
 * 
 */
@Stateless
@Path("/projects")
public class ProjectEndpoint
{
  @PersistenceContext(unitName = "test-pe")
  private EntityManager em;

  @POST
  @Consumes("application/json")
  public Response create(Project entity)
  {
    /*for (Member member : entity.getMembers())
    {
      Member m = em.find(Member.class, member.getId());
      entity.getMembers().add(m);
      m.getProjects().add(entity);
    }*/
    em.persist(entity);
    return Response.created(UriBuilder.fromResource(ProjectEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  public Response deleteById(@PathParam("id") Long id)
  {
    Response resp = Response.noContent().build();
    Project entity = em.find(Project.class, id);
    if (entity == null)
      resp = Response.status(Status.NOT_FOUND).build();
    em.remove(entity);
    return resp;
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces("application/json")
  public Response findById(@PathParam("id") Long id)
  {
    Response resp = null;
    TypedQuery<Project> findByIdQuery = em.createQuery("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.members WHERE p.id = :entityId ORDER BY p.id", Project.class);
    findByIdQuery.setParameter("entityId", id);
    Project entity = null;
    try
    {
      entity = findByIdQuery.getSingleResult();
      resp = Response.ok(entity).build();
    }
    catch (NoResultException nre)
    {
      resp = Response.status(Status.NOT_FOUND).build();
    }
    return resp;
  }

  @GET
  @Produces("application/json")
  public List<Project> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult)
  {
    TypedQuery<Project> findAllQuery = em.createQuery("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.members ORDER BY p.id", Project.class);
    if (startPosition != null)
      findAllQuery.setFirstResult(startPosition);
    if (maxResult != null)
      findAllQuery.setMaxResults(maxResult);
    return findAllQuery.getResultList();
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes("application/json")
  public Response update(@PathParam("id") Long id, Project entity)
  {
    Response resp = null;
    
    if (entity == null)
      resp = Response.status(Status.BAD_REQUEST).build();
    if (id == null)
      resp = Response.status(Status.BAD_REQUEST).build();
    if (!id.equals(entity.getId()))
      resp = Response.status(Status.CONFLICT).entity(entity).build();
    if (em.find(Project.class, id) == null)
      resp = Response.status(Status.NOT_FOUND).build();
    try
    {
      entity = em.merge(entity);
      resp = Response.noContent().build();
    }
    catch (OptimisticLockException e)
    {
      resp = Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
    }
    return resp;
  }
}
