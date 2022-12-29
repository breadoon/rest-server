/*******************************************************************************
 * This file is part of the breadoon project.
 * Copyright (c) 2022-2022 breadoon@gmail.com
 * Authors: breadoon@gmail.com.
 * This program is offered under a commercial and under the AGPL license.
 * For commercial licensing, contact breadoon@gmail.com.  For AGPL licensing, see below.
 * AGPL licensing:
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package xyz.breadoon.rest.util;

import java.security.*;

/**
 * @author Administrator
 *
 */
public abstract class CryptoFactory
{
	protected byte[] m_baKey;

	public abstract byte[] encrypt( byte[] baSrc ) throws Exception;
	public abstract byte[] decrypt( byte[] baSrc ) throws Exception;
	
	/** 
	 * 
	 * 생성자
	 *
	 */
	public CryptoFactory() {
		Security.addProvider( new org.bouncycastle.jce.provider.BouncyCastleProvider() );
	}
	
	
	/**
	 * 
	 * Method Name : <br>
	 * Method Description :
	 * @param sSrc
	 * @return
	 * @throws Exception
	 */
	public byte[] encrypt( String sSrc ) throws Exception {
		byte[] result = null;

		try {
			result = encrypt( sSrc.getBytes() );
		} catch( Exception e ) {
			throw e;
		} finally {}
		
		return result;
	}

	
	/**
	 * 
	 * Method Name : <br>
	 * Method Description :
	 * @param sSrc
	 * @return
	 * @throws Exception
	 */
	public byte[] decrypt( String sSrc ) throws Exception {
		byte[] result = null;
		try {
			result = decrypt( sSrc.getBytes() );
		} catch( Exception e ) {
			throw e;
		} finally {}
		
		return result;
	}
	
	
	/**
	 * 
	 * Method Name : <br>
	 * Method Description :
	 * @return
	 */
	public byte[] getKey() {
		return m_baKey;
	}

	
	/**
	 * 
	 * Method Name : <br>
	 * Method Description :
	 * @param baKey
	 * @throws Exception
	 */
	public void setKey( byte[] baKey ) throws Exception {
		
		if( baKey.length != 32)
			throw new Exception("key size must be 32 length");
		
		m_baKey = new byte[ baKey.length ];
		System.arraycopy( baKey, 0, m_baKey, 0, baKey.length );
	}
	
	/**
	 * 
	 * Method Name : <br>
	 * Method Description :
	 * @param strKey
	 * @throws Exception
	 */
	public void setKeyString( String strKey ) throws Exception {
		byte[] baKey = strKey.getBytes();
		setKey( baKey );
	}
	
	/**
	 * 
	 * Method Name : <br>
	 * Method Description : SHA256으로 해슁을 해서 32 바이트 키를 리턴한다.
	 * @param baPreKey
	 * @return
	 * @throws Exception
	 */
	public byte[] hashSHA256( byte[] baPreKey ) throws Exception {
		byte[]			baResult = null;
		byte[]			ba32bytes = null;
		int				nKeyLen;
		
		MessageDigest	md = MessageDigest.getInstance( "SHA-256" );	
		
		try {
			// 앞의 32 bytes만 사용..
			nKeyLen = baPreKey.length > 32 ? 32 : baPreKey.length;
			ba32bytes = new byte[ nKeyLen ];
			//
			// 32바이트만 복사
			//
			System.arraycopy( baPreKey, 0, ba32bytes, 0, nKeyLen );
			baResult = md.digest( ba32bytes );
		} catch( Exception e ) {
			throw e;
		} finally {}
		
		return baResult;
	}


	/**
	 * 
	 * Method Name : <br>
	 * Method Description :
	 * @param buffer
	 * @return
	 * @throws Exception
	 */
	public static String BinaryToHexString( byte buffer[] ) throws Exception {
		StringBuffer buf = new StringBuffer();
		int nValue;
		
		try {
			for( int i = 0; i < buffer.length; i++ ) {
				nValue = (int)buffer[ i ] & 0x00ff;
				if( nValue < 16 ) buf.append( "0" );
				buf.append( Integer.toHexString( nValue ).toUpperCase() );
			}
		} catch( Exception e ) {
			throw e;
		} finally {}
		
		return buf.toString();
	}


	/**
	 * 
	 * Method Name : <br>
	 * Method Description :
	 * @param strHex
	 * @return
	 * @throws Exception
	 */
	public static byte[] HexStringToBinary(String strHex) throws Exception {
		byte[] baHex = strHex.getBytes();
		byte[] baBin = new byte[baHex.length/2];
		byte b=0;

		try {
			for(int i=0; i < baHex.length; i++) {
				switch(baHex[i]) 
				{
				case '0': case '1': case '2': case '3': case '4':
				case '5': case '6': case '7': case '8': case '9' :
					b += baHex[i] - (byte)'0';
					break;
				case 'a': case 'b': case 'c': 
				case 'd': case 'e': case 'f':
					b += baHex[i] - (byte)'a' + 10;
					break;
				case 'A': case 'B': case 'C':
				case 'D': case 'E': case 'F':
					b += baHex[i] - (byte)'A' + 10;
					break;
				}
				if(i % 2 == 1) {
					baBin[i/2] = b;
					b = 0;
				}
				else {
					b *= 16;
				}
			}
		} catch( Exception e ) {
			throw e;
		} finally {}

		return baBin;
	}
	
	/**
	 * 
	 * Method Name : <br>
	 * Method Description : Block 암호화 algorithm을 사용하는 경우 직접 block size에 맞게 0으로 padding
	 * @param baSrc
	 * @param nBlcokSize
	 * @return
	 * @throws Exception
	 */
	public static byte[] getPaddedData(byte[] baSrc, int nBlockSize) throws Exception  { 
		
		// padding 된 크기를 구함.
		int nNewLen = ( ( baSrc.length + nBlockSize-1 ) / nBlockSize ) * nBlockSize;

		//
		// 생성,  0 으로 초기화
		//
		byte[] baPadded = new byte[ nNewLen ];
		for( int i = 0; i < nNewLen; i++ ) 
			baPadded[ i ] = 0;
			
		//
		// 데이타 복사 
		//
		System.arraycopy( baSrc, 0, baPadded, 0, baSrc.length );

		return baPadded;
	}
			
	/**
	 * 
	 * Method Name : <br>
	 * Method Description :  Encryption시 Padding된 데이타('\0')을 제거한 String을 생성  
	 * @param baData
	 * @return
	 * @throws Exception
	 */
	public static String getTrimedString(byte[] baData) throws Exception  {
		int baLen = baData.length;	
		while( baLen > 0 && baData[baLen-1] == (byte)0)
			baLen--;
		return new String(baData, 0, baLen);
	}
	
		/**
		 * 
		 * Method Name : <br>
		 * Method Description :  Asymmetric KeyPair(Public/Private key pair)를 생성
		 * @param sAlgorithm : <RSA | DSA>
		 * @param nKeyLength
		 * @return
		 * @throws Exception
		 */
      	public static KeyPair generateKeyPair(String sAlgorithm, int nKeyLength) throws Exception {
      		KeyPair keyPair = null;

			try {
				KeyPairGenerator generator = KeyPairGenerator.getInstance(sAlgorithm);
				generator.initialize(nKeyLength);
				keyPair = generator.generateKeyPair(); 
	
	 		/*} catch (NoSuchAlgorithmException e) {
	          		System.err.println(
				    "usage: java AsymmetricKeyMaker <RSA | DSA>");
			*/
			} catch( Exception e ) {
				throw e;
			} finally {}
		 
			return keyPair;
      	}
      	
      	
} // end class
