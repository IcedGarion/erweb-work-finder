package it.erweb.crawler.database.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * The persistent class for the notifica database table.
 * 
 */
@Entity
@NamedQuery(name = "Notifica.findAll", query = "SELECT n FROM Notifica n")
public class Notifica extends AbstractModel implements Serializable
{
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private NotificaPK id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_NOTIFICA")
	private Date dtNotifica;

	private String stato;

	public Notifica()
	{
	}

	public NotificaPK getId()
	{
		return this.id;
	}

	public void setId(NotificaPK id)
	{
		this.id = id;
	}

	public Date getDtNotifica()
	{
		return this.dtNotifica;
	}

	public void setDtNotifica(Date dtNotifica)
	{
		this.dtNotifica = dtNotifica;
	}

	public String getStato()
	{
		return this.stato;
	}

	public void setStato(String stato)
	{
		this.stato = stato;
	}

	@Override
	public String toString()
	{
		return this.stato + ", " + this.dtNotifica + ", usr: " + this.id.getCdUtente() + ", ban: " + this.id.getCdBando();
	}
}