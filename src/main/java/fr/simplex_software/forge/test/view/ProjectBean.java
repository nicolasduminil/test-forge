package fr.simplex_software.forge.test.view;

import java.io.*;
import java.util.*;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.context.*;
import javax.faces.application.*;
import javax.faces.component.*;
import javax.faces.context.*;
import javax.faces.convert.*;
import javax.inject.*;
import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.servlet.http.*;

import fr.simplex_software.forge.test.model.*;

/**
 * Backing bean for Project entities.
 * <p/>
 * This class provides CRUD functionality for all Project entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class ProjectBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Project entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Project project;

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@Inject
	private Conversation conversation;

	@PersistenceContext(unitName = "test-pe", type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public String create() {

		this.conversation.begin();
		this.conversation.setTimeout(1800000L);
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.conversation.isTransient()) {
			this.conversation.begin();
			this.conversation.setTimeout(1800000L);
		}

		if (this.id == null) {
			this.project = this.example;
		} else {
			this.project = findById(getId());
		}
	}

	public Project findById(Long id) {

		return this.entityManager.find(Project.class, id);
	}

	/*
	 * Support updating and deleting Project entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.project);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.project);
				return "view?faces-redirect=true&id=" + this.project.getId();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {
		this.conversation.end();

		try {
			Project deletableEntity = findById(getId());

			this.entityManager.remove(deletableEntity);
			this.entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching Project entities with pagination
	 */

	private int page;
	private long count;
	private List<Project> pageItems;

	private Project example = new Project();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Project getExample() {
		return this.example;
	}

	public void setExample(Project example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Project> root = countCriteria.from(Project.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Project> criteria = builder.createQuery(Project.class);
		root = criteria.from(Project.class);
		TypedQuery<Project> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Project> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String name = this.example.getName();
		if (name != null && !"".equals(name)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("name")),
					'%' + name.toLowerCase() + '%'));
		}
		String description = this.example.getDescription();
		if (description != null && !"".equals(description)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("description")),
					'%' + description.toLowerCase() + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Project> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Project entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Project> getAll() {

		CriteriaQuery<Project> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(Project.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Project.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final ProjectBean ejbProxy = this.sessionContext
				.getBusinessObject(ProjectBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context,
					UIComponent component, String value) {

				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context,
					UIComponent component, Object value) {

				if (value == null) {
					return "";
				}

				return String.valueOf(((Project) value).getId());
			}
		};
	}

	private Project add = new Project();

	public Project getAdd() {
		return this.add;
	}

	public Project getAdded() {
		Project added = this.add;
		this.add = new Project();
		return added;
	}
	
	public String addMember(Member newMember)
	{
	  project.getMembers().add(newMember);
	  newMember.getProjects().add(project);
    entityManager.merge(project);
    return "view?faces-redirect=true&id=" + project.getId();
	}
	
	public String removeMember (Object o)
	{
	  System.out.println("*** removeMember: " + o);
	  HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
	  Enumeration<String> names = req.getAttributeNames();
	  while (names.hasMoreElements())
	  {
	    String name = names.nextElement();
	    System.out.println("*** " + name + " ->" + req.getAttribute(names.nextElement()));
	  }
	  Member newMember = (Member)o;
	  if (newMember != null && newMember.getProjects() != null)
	    newMember.getProjects().remove(project);
	  project.getMembers().remove(newMember);
    entityManager.merge(project);
    return "view?faces-redirect=true&id=" + project.getId();
	}
}
