package fr.simplex_software.forge.test.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

@Entity
@XmlRootElement
public class Member implements Serializable
{
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Version
  @Column(name = "version")
  private int version;

  @Column(length = 40, name = "FIRST_NAME", nullable = false)
  private String firstName;

  @Column(length = 40, name = "LAST_NAME", nullable = false)
  private String lastName;

  @Column(name = "POSTAL_ADDRESS", nullable = false)
  private String address;

  @Column(name = "EMAIL_ADDRESS", nullable = false)
  private String emailAddress;

  @ManyToMany(cascade=CascadeType.MERGE)
  @JoinTable(name = "MEMBER_PROJECT", joinColumns = @JoinColumn(name = "MEMBER_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "PROJECT_ID", referencedColumnName = "ID"))
  private Set<Project> projects = new HashSet<Project>();

  public Member()
  {
  }

  public Member(Long id, int version, String firstName, String lastName, String address, String emailAddress)
  {
    this.id = id;
    this.version = version;
    this.firstName = firstName;
    this.lastName = lastName;
    this.address = address;
    this.emailAddress = emailAddress;
  }

  public Long getId()
  {
    return this.id;
  }

  public void setId(final Long id)
  {
    this.id = id;
  }

  public int getVersion()
  {
    return this.version;
  }

  public void setVersion(final int version)
  {
    this.version = version;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public String getAddress()
  {
    return address;
  }

  public String getEmailAddress()
  {
    return emailAddress;
  }

  public void setFirstName(String firstName)
  {
    this.firstName = firstName;
  }

  public void setLastName(String lastName)
  {
    this.lastName = lastName;
  }

  public void setAddress(String address)
  {
    this.address = address;
  }

  public void setEmailAddress(String emailAddress)
  {
    this.emailAddress = emailAddress;
  }

  public Set<Project> getProjects()
  {
    return this.projects;
  }

  public void setProjects(final Set<Project> projects)
  {
    this.projects = projects;
  }

  @Override
  public String toString()
  {
    String result = getClass().getSimpleName() + " ";
    if (id != null)
      result += "id: " + id;
    result += ", version: " + version;
    if (firstName != null && !firstName.trim().isEmpty())
      result += ", firstName: " + firstName;
    if (lastName != null && !lastName.trim().isEmpty())
      result += ", lastName: " + lastName;
    if (address != null)
      result += ", address: " + address;
    if (emailAddress != null)
      result += ", emailAddress: " + emailAddress;
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    boolean result = true;
    if (this != obj)
      result = false;
    if (!(obj instanceof Member))
       result = false;
    Member other = (Member) obj;
    if (id != null)
      if (!id.equals(other.id))
        result =  false;
    return result;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

}