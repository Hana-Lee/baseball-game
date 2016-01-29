package kr.co.leehana.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import java.io.Serializable;

/**
 * @author Hana Lee
 * @since 2016-01-28 15:46
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchRecord extends BasicModel implements Serializable {

	private static final long serialVersionUID = -3739036334295763797L;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@PrimaryKeyJoinColumn
	private TotalGame totalGame;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@PrimaryKeyJoinColumn
	private Win win;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@PrimaryKeyJoinColumn
	private Lose lose;
}
