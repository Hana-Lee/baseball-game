package kr.co.leehana.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.co.leehana.enums.Enabled;
import kr.co.leehana.enums.GameRole;
import kr.co.leehana.enums.Status;
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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
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
	@JoinColumn(name = "avatar_id")
	private Avatar avatar;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "level_id")
	@NotNull
	private Level level;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "total_score_id")
	@NotNull
	private TotalScore totalScore;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "total_rank_id")
	@NotNull
	private TotalRank totalRank;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "match_record_id")
	@NotNull
	private MatchRecord matchRecord;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "attacker_role_count_id")
	@NotNull
	private AttackerRoleCount attackerRoleCount;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "defender_role_count_id")
	@NotNull
	private DefenderRoleCount defenderRoleCount;

	@Enumerated(EnumType.STRING)
	private GameRole gameRole;

	@Enumerated(EnumType.STRING)
	private Status status;

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date updated;

	@Temporal(TemporalType.TIMESTAMP)
	private Date deleted;

	@NotNull
	private Boolean admin;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Enabled enabled;

	private Integer inputCount = 0;

	@Transient
	private String guessNumber;

	@Temporal(TemporalType.TIMESTAMP)
	private Date gameOverTime;
}
