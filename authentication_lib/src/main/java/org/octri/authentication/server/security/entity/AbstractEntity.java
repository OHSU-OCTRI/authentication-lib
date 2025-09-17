package org.octri.authentication.server.security.entity;

import java.io.Serializable;

import org.octri.authentication.server.view.Identified;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * An abstract entity for all other entities to extend. It includes the base {@link #id} field which is
 * auto-incrementing.
 *
 * @deprecated
 *             Use {@link org.octri.common.domain.AbstractEntity} from
 *             <a href="https://github.com/OHSU-OCTRI/common-lib">OCTRI common-lib</a> instead.
 *
 * @author yateam
 */
@MappedSuperclass
@Deprecated(since = "2.3.0", forRemoval = true)
public abstract class AbstractEntity implements Serializable, Identified {

	private static final long serialVersionUID = 3042616837618435959L;

	/**
	 * Unique identifier for the entity.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	protected Long id;

	/**
	 * Default constructor.
	 */
	public AbstractEntity() {
		super();
	}

	/**
	 * Convenience constructor that sets the {@link #id} property.
	 *
	 * @param id
	 *            unique identifier value
	 */
	public AbstractEntity(Long id) {
		super();
		this.id = id;
	}

	/**
	 * Gets the entity's ID.
	 */
	@Override
	public Long getId() {
		return this.id;
	}

	/**
	 * Sets the entity's ID.
	 *
	 * @param id
	 *            unique identifier value
	 */
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "AbstractEntity [id=" + this.id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AbstractEntity)) {
			return false;
		}
		AbstractEntity other = (AbstractEntity) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
