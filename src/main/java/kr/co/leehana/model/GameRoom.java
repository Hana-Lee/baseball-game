package kr.co.leehana.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hana Lee
 * @since 2016-01-30 20:48
 */
//@Entity
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
	private long id;

	@NotNull
	private String name;

	private int limit = 5;

	@NotNull
	@Column(unique = true)
	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = false)
	@JoinColumn(name = "player_id")
	private Player owner;

	@OneToMany(cascade = {CascadeType.ALL}, targetEntity = Player.class, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<Player> users = new LinkedHashSet<>();

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "setting_id")
	private Setting setting;
	private int gameCount = 0;
	private String generationNumbers;

	public GameRoom(long id, String name, Player owner, int limit, Setting setting) {
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.limit = limit;
		this.setting = setting;
	}

	public GameRoom(long id, String name, int limit, Setting setting) {
		this.id = id;
		this.name = name;
		this.limit = limit;
		this.setting = setting;
	}
}
