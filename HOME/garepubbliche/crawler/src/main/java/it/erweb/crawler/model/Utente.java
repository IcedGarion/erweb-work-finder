package it.erweb.crawler.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the utente database table.
 * 
 */
@Entity
@NamedQuery(name = "Utente.findAll", query = "SELECT u FROM Utente u")
public class Utente implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CD_UTENTE")
	private long cdUtente;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_NOTIFICA")
	private Date dtNotifica;

	private String email;

	private String password;

	private String username;

	// bi-directional many-to-one association to Expreg
	@OneToMany(mappedBy = "utente")
	private List<Expreg> expregs;

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

	public List<Expreg> getExpregs()
	{
		return this.expregs;
	}

	public void setExpregs(List<Expreg> expregs)
	{
		this.expregs = expregs;
	}

	public Expreg addExpreg(Expreg expreg)
	{
		getExpregs().add(expreg);
		expreg.setUtente(this);

		return expreg;
	}

	public Expreg removeExpreg(Expreg expreg)
	{
		getExpregs().remove(expreg);
		expreg.setUtente(null);

		return expreg;
	}

}