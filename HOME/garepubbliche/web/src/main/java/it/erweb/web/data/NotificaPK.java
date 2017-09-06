package it.erweb.web.data;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the notifica database table.
 * 
 */
@Embeddable
public class NotificaPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="CD_UTENTE", insertable=false, updatable=false)
	private long cdUtente;

	@Column(name="CD_BANDO", insertable=false, updatable=false)
	private long cdBando;

	public NotificaPK() {
	}
	public long getCdUtente() {
		return this.cdUtente;
	}
	public void setCdUtente(long cdUtente) {
		this.cdUtente = cdUtente;
	}
	public long getCdBando() {
		return this.cdBando;
	}
	public void setCdBando(long cdBando) {
		this.cdBando = cdBando;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof NotificaPK)) {
			return false;
		}
		NotificaPK castOther = (NotificaPK)other;
		return 
			this.cdUtente == (castOther.cdUtente)
			&& this.cdBando == (castOther.cdBando);
	}

	public int hashCode() {
		/*final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.cdUtente.hashCode();
		hash = hash * prime + this.cdBando.hashCode();
		*/
		return 1;
	}
}