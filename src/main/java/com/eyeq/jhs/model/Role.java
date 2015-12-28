package com.eyeq.jhs.model;

import com.eyeq.jhs.type.RoleType;
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

	private RoleType roleType = RoleType.ATTACKER;
}
