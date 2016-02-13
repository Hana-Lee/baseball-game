package kr.co.leehana.security;

import kr.co.leehana.model.Player;
import kr.co.leehana.enums.PlayerRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Hana Lee
 * @since 2016-02-04 10:57
 */
public class UserDetailsImpl extends User {

	public UserDetailsImpl(Player player) {
		super(player.getEmail(), player.getPassword(), makeAuthorities(player));
	}

	private static Collection<? extends GrantedAuthority> makeAuthorities(final Player player) {
		final List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(PlayerRole.USER.getRoleName()));

		if (player.getAdmin() != null && player.getAdmin()) {
			authorities.add(new SimpleGrantedAuthority(PlayerRole.ADMIN.getRoleName()));
		}

		return authorities;
	}

	public String getEmail() {
		return super.getUsername();
	}
}
