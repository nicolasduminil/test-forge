package fr.simplex_software.forge.test.rest;

import java.util.*;

import javax.ejb.*;
import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.*;

import fr.simplex_software.forge.test.model.*;

@Stateless
@Path("/members")
public class MemberEndpoint
{
  @PersistenceContext(unitName = "test-pe")
  private EntityManager em;

  @POST
  @Consumes("application/json")
  public Response create(Member entity)
  {
    em.persist(entity);
    return Response.created(UriBuilder.fromResource(MemberEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  public Response deleteById(@PathParam("id") Long id)
  {
    Member entity = em.find(Member.class, id);
    if (entity == null)
    {
      return Response.status(Status.NOT_FOUND).build();
    }
    em.remove(entity);
    return Response.noContent().build();
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces("application/json")
  public Response findById(@PathParam("id") Long id)
  {
    TypedQuery<Member> findByIdQuery = em.createQuery("SELECT DISTINCT m FROM Member m LEFT JOIN FETCH m.projects WHERE m.id = :entityId ORDER BY m.id", Member.class);
    findByIdQuery.setParameter("entityId", id);
    Member entity;
    try
    {
      entity = findByIdQuery.getSingleResult();
    }
    catch (NoResultException nre)
    {
      entity = null;
    }
    if (entity == null)
    {
      return Response.status(Status.NOT_FOUND).build();
    }
    return Response.ok(entity).build();
  }

  @GET
  @Produces("application/json")
  public List<Member> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult)
  {
    TypedQuery<Member> findAllQuery = em.createQuery("SELECT DISTINCT m FROM Member m LEFT JOIN FETCH m.projects ORDER BY m.id", Member.class);
    if (startPosition != null)
      findAllQuery.setFirstResult(startPosition);
    if (maxResult != null)
      findAllQuery.setMaxResults(maxResult);
    final List<Member> results = findAllQuery.getResultList();
    return results;
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes("application/json")
  public Response update(@PathParam("id") Long id, Member entity)
  {
    if (entity == null)
    {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (id == null)
    {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (!id.equals(entity.getId()))
    {
      return Response.status(Status.CONFLICT).entity(entity).build();
    }
    if (em.find(Member.class, id) == null)
    {
      return Response.status(Status.NOT_FOUND).build();
    }
    try
    {
      for (Project project : entity.getProjects())
      {
        Project p = em.find(Project.class, project.getId());
        project.setName(p.getName());
        project.setDescription(p.getDescription());
        project.setVersion(p.getVersion());
      }
      entity = em.merge(entity);
    }
    catch (OptimisticLockException e)
    {
      return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
    }

    return Response.noContent().build();
  }
}
