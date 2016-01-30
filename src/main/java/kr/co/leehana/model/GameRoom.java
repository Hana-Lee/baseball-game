package kr.co.leehana.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hana Lee
 * @since 2015-12-23 22:38
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
@ToString
public class GameRoom implements Serializable {

	private static final long serialVersionUID = 1663832219098767828L;

	@Id
	@GeneratedValue
	@Column(name = "game_room_id")
	@Setter(AccessLevel.NONE)
	private long id;

	private String name;
	private int limit = 5;
	private OldUser owner;
	private Set<OldUser> users = new LinkedHashSet<>();
	private Setting setting;
	private int gameCount = 0;
	private String generationNumbers;

	public GameRoom(long id, String name, OldUser owner, int limit, Setting setting) {
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
