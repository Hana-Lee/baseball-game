package kr.co.leehana.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Hana Lee
 * @since 2016-01-28 16:59
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class Player implements Serializable {

	private static final long serialVersionUID = -1278003433728981977L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "player_id")
	@Setter(AccessLevel.NONE)
	private Long id;

	@Column(unique = true)
	@NotNull
	@Email
	private String email;

	@Column(unique = true)
	@NotNull
	private String nickname;

	@JsonIgnore
	@NotNull
	private String password;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "level_id")
	@NotNull
	private Level level;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "total_rank_id")
	@NotNull
	private TotalRank totalRank;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "match_record_id")
	@NotNull
	private MatchRecord matchRecord;

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date joined;

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date updated;

	@NotNull
	private Boolean admin;
}
