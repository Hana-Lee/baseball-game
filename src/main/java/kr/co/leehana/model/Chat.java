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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

/**
 * @author Hana Lee
 * @since 2016-02-25 21:16
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class Chat implements Serializable {

	private static final long serialVersionUID = 3055921412133797685L;

	@Id
	@GeneratedValue
	@Column(name = "chat_id")
	@Setter(AccessLevel.NONE)
	private Long id;

	@NotNull
	@OneToOne(cascade = {MERGE, REFRESH, DETACH}, optional = true, fetch = EAGER, orphanRemoval = false)
	@JoinColumn(name = "player_id")
	private Player player;

	@OneToOne(cascade = {MERGE, REFRESH, DETACH}, optional = true, fetch = EAGER, orphanRemoval = false)
	@JoinColumn(name = "game_room_id")
	private GameRoom gameRoom;

	// 채팅 메세지
	private String message;

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date created;
}
