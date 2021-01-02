package com.kiran.securesockets.security.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.*;


public class PasswordUtil {
	private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);
	private static final String EMPTY = "";
	private static final String UPPER_CASE_ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWER_CASE_ALPHABETS = "abcdefghijklmnopqrstuvwxyz";
	private static final String DIGITS = "0123456789";
	private static final String SPECIAL_CHARS = "!@#$%&?";
	private static final String MIXED_CHARS = UPPER_CASE_ALPHABETS + LOWER_CASE_ALPHABETS + DIGITS + SPECIAL_CHARS;

	private static final int FIXED_LENGTH = 8;
	private static final float UPPERCASE_RATIO = 12.5f;
	private static final float LOWERCASE_RATIO = 37.5f;
	private static final float DIGIT_RATIO = 25.0f;
	private static final float SPECIAL_CHAR_RATIO = 25.0f;

	public static final int AES_KEY_SIZE = 256;
	public static final int GCM_TAG_LENGTH = 16;
	private static final String SECRET_KEY_FACTORY = "PBKDF2WithHmacSHA256";
	private static final String PADDING_MODE = "AES/GCM/NoPadding";

	private static final String INIT_VECTOR = System.getenv("password-encryption-init-vector");

	private static final String LENGTH_ERROR = "Invalid password length found. Only >= 8 length is allowed.";

	private static Random random = new SecureRandom();

	private PasswordUtil() {
		throw new IllegalStateException("Implementation is not allowed for this class.");
	}

	public static String encrypt(String str, String secretKey, String salt) {
		try {
			IvParameterSpec ivParamSpec = new IvParameterSpec(INIT_VECTOR.getBytes());
			SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY);
			KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, ivParamSpec.getIV());
			Cipher cipher = Cipher.getInstance(PADDING_MODE);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
			return asciiToHex(Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8))));
		} catch (Exception e) {
			logger.error("{}", e);
		}
		return EMPTY;
	}

	public static String decrypt(String str, String secretKey, String salt) {
		try {
			IvParameterSpec ivParamSpec = new IvParameterSpec(INIT_VECTOR.getBytes());
			SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY);
			KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, ivParamSpec.getIV());
			Cipher cipher = Cipher.getInstance(PADDING_MODE);
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
			return new String(cipher.doFinal(Base64.getDecoder().decode(hexToAscii(str))));
		} catch (Exception e) {
			logger.error("{}", e);
		}
		return EMPTY;
	}

	public static String generateSecretKey() {
		try {
			return digestMessage(generateRandomString(16) + "_" + System.currentTimeMillis());
		} catch (NoSuchAlgorithmException e) {
			logger.error("Something went wrong while generating secret key, please try again.");
		}
		return EMPTY;
	}

	public static String generateSalt() {
		try {
			return digestMessage(generateRandomString(8) + "_" + System.currentTimeMillis());
		} catch (NoSuchAlgorithmException e) {
			logger.error("Something went wrong while generating tasty salt, please try again.");
		}
		return EMPTY;
	}

	public static String generateRandomString(int length) {
		if (FIXED_LENGTH > length) {
			throw new IllegalArgumentException(String.format("To generate random string, minimum %s is required.", FIXED_LENGTH));
		} else {
			StringBuilder randomStringBuilder = new StringBuilder();
			for (int i = 0; i < length; i++) {
				int index = random.nextInt(MIXED_CHARS.length());
				char selectedChar = MIXED_CHARS.charAt(index);
				randomStringBuilder.append(selectedChar);
			}
			return shuffleChars(randomStringBuilder.toString());
		}
	}

	public static String digestMessage(String str) throws NoSuchAlgorithmException {
		final byte[] bytes = str.getBytes();
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(bytes);
		final byte[] digestedBytes = messageDigest.digest();
		return generateHexString(digestedBytes);
	}

	public static String generateRandomPassword(int length) {
		if (FIXED_LENGTH > length) {
			throw new IllegalArgumentException(LENGTH_ERROR + " => " + length);
		}
		StringBuilder builder = new StringBuilder();
		int u = guessUpperCaseCharCount(length);
		int l = guessLowerCaseCharCount(length);
		int d = guessDigitCharCount(length);
		int s = guessSpecialCharCount(length);
		writePassword(UPPER_CASE_ALPHABETS, (1 == u) ? u : u - 1, builder);
		writePassword(LOWER_CASE_ALPHABETS, (1 == l) ? l : l - 1, builder);
		writePassword(DIGITS, (1 == d) ? d : d - 1, builder);
		writePassword(SPECIAL_CHARS, (1 == s) ? s : s - 1, builder);
		return shuffleChars(builder.toString());
	}

	private static int guessUpperCaseCharCount(int passwordLength) {
		return Math.round((passwordLength * UPPERCASE_RATIO) / FIXED_LENGTH) / FIXED_LENGTH;
	}

	private static int guessLowerCaseCharCount(int passwordLength) {
		return Math.round((passwordLength * LOWERCASE_RATIO) / FIXED_LENGTH) / FIXED_LENGTH;
	}

	private static int guessDigitCharCount(int passwordLength) {
		return Math.round((passwordLength * DIGIT_RATIO) / FIXED_LENGTH) / FIXED_LENGTH;
	}

	private static int guessSpecialCharCount(int passwordLength) {
		return Math.round((passwordLength * SPECIAL_CHAR_RATIO) / FIXED_LENGTH) / FIXED_LENGTH;
	}

	private static void writePassword(String targetCharArr, int maxCharCount, StringBuilder builder) {
		for (int i = 0; i < maxCharCount; i++) {
			int index = random.nextInt(targetCharArr.length());
			char selectedChar = targetCharArr.charAt(index);
			builder.append(selectedChar);
		}
	}

	private static String shuffleChars(String pass) {
		List<String> generatedChars = Arrays.asList(pass.split(""));
		Collections.shuffle(generatedChars);
		return String.join("", generatedChars);
	}


	public static String generateHexString(byte[] rawBytes) {
		StringBuilder hexBuilder = new StringBuilder();
		for (int i = 0; i < rawBytes.length; i++) {
			String hex = Integer.toHexString(0xff & rawBytes[i]);
			if (hex.length() == 1) {
				hexBuilder.append('0');
			}
			hexBuilder.append(hex);
		}
		return hexBuilder.toString();
	}

	public static final String asciiToHex(String asciiStr) {
		char[] chars = asciiStr.toCharArray();
		StringBuilder hex = new StringBuilder();
		for (char ch : chars) {
			hex.append(Integer.toHexString((int) ch));
		}

		return hex.toString();
	}

	public static final String hexToAscii(String hexStr) {
		StringBuilder output = new StringBuilder("");

		for (int i = 0; i < hexStr.length(); i += 2) {
			String str = hexStr.substring(i, i + 2);
			output.append((char) Integer.parseInt(str, 16));
		}

		return output.toString();
	}

}
