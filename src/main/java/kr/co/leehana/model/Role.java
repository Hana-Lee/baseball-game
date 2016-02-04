package kr.co.leehana.model;

import kr.co.leehana.type.GameRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hana Lee
 * @since 2015-12-27 05:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

	private GameRole roleType = GameRole.ATTACKER;
}
