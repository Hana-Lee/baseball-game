package com.eyeq.lhn;

import org.junit.Test;

import java.util.Objects;

/**
 * @author Hana Lee
 * @since 2015-12-30 23-21
 */
public class DummyTest {

	@Test
	public void test() {
		int a = 10;
		int b = 11;

		if (a == b) {
			System.out.println("a == b");
		} else {
			System.out.println("a != b");
		}

		Integer aa = 10;
		Integer bb = 10;

		if (Objects.equals(aa, bb)) {
			System.out.println("aa == bb");
		} else {
			System.out.println("aa != bb");
		}

		long aaa = 10;
		int bbb = 10;
		if (aaa == bbb) {
			System.out.println("aaa == bbb");
		} else {
			System.out.println("aaa != bbb");
		}
	}
}
