package it.erweb.crawler.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * The persistent class for the utente database table.
 * 
 */
@Entity
@NamedQuery(name = "Utente.findAll", query = "SELECT u FROM Utente u")
public class Utente extends AbstractModel implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "CD_UTENTE")
	private long cdUtente;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_NOTIFICA")
	private Date dtNotifica;

	private String email;

	private String password;

	private String username;

	// bi-directional many-to-one association to Expreg
	@OneToOne(mappedBy = "utente")
	private Expreg expreg;

	public Utente()
	{
	}

	public long getCdUtente()
	{
		return this.cdUtente;
	}

	public void setCdUtente(long cdUtente)
	{
		this.cdUtente = cdUtente;
	}

	public Date getDtNotifica()
	{
		return this.dtNotifica;
	}

	public void setDtNotifica(Date dtNotifica)
	{
		this.dtNotifica = dtNotifica;
	}

	public String getEmail()
	{
		return this.email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPassword()
	{
		return this.password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getUsername()
	{
		return this.username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public Expreg getExpreg()
	{
		return this.expreg;
	}

	public void setExpreg(Expreg expreg)
	{
		this.expreg = expreg;
	}

	@Override
	public String toString()
	{
		return this.cdUtente + ", " + this.username + ", " + this.password + ", " + this.email;
	}

}