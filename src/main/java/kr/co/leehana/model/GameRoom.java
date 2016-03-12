package kr.co.leehana.model;

import kr.co.leehana.enums.Enabled;
import kr.co.leehana.enums.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

/**
 * @author Hana Lee
 * @since 2016-01-30 20:48
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
@ToString
public class GameRoom implements Serializable {

	private static final long serialVersionUID = 3111734724040167384L;

	@Id
	@GeneratedValue
	@Column(name = "game_room_id")
	@Setter(AccessLevel.NONE)
	private Long id;

	@NotNull
	private String name;

	@NotNull
	private Integer roomNumber;

	private Integer limitPlayerCount = 5;

	@Enumerated(EnumType.STRING)
	private Status status;

	@NotNull
	@OneToOne(cascade = {MERGE, REFRESH, DETACH}, optional = true, fetch = EAGER, orphanRemoval = false)
	@JoinColumn(name = "owner_id")
	private Player owner;

	@OneToMany(cascade = {MERGE, REFRESH, DETACH}, fetch = EAGER, orphanRemoval = false)
	@JoinColumn(name = "joined_room_id")
	private Set<Player> players = new LinkedHashSet<>();

	@OneToOne(cascade = {PERSIST, MERGE, REMOVE, REFRESH, DETACH}, optional = false, fetch = EAGER, orphanRemoval = true)
	@JoinColumn(name = "setting_id")
	private Setting setting;

	private Integer gameCount = 0;

	@OneToOne(cascade = {PERSIST, MERGE, REMOVE, REFRESH, DETACH}, optional = true, fetch = EAGER, orphanRemoval = false)
	@JoinColumn(name = "random_number_id")
	private RandomNumber randomNumber;

	@Transient
	private Map<Integer, Player> playerRankMap = new HashMap<>();

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date updated;

	@Temporal(TemporalType.TIMESTAMP)
	private Date deleted;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Enabled enabled;
}
