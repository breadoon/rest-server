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

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.Key;

/**
 * @author Administrator
 *
 */
public class CryptoDESedeEx extends CryptoFactory
{
	
	
	public String encryptToHexString( String rawData )throws Exception{
		return CryptoDESedeEx.BinaryToHexString( encrypt( rawData.getBytes() ) );
	}
	
	public String decryptFromHexString( String tDESData )throws Exception{
		return new String( decrypt( CryptoDESedeEx.HexStringToBinary( tDESData ) ) ).trim();
	}
	
	/**
	 * 
	 * Method Name :
	 * @param baSrc
	 * @return
	 * @throws Exception
	 * @see unidocs.drm.cryptography.CryptoFactory#encrypt(byte[])
	 */
	public byte[] encrypt( byte[] baSrc ) throws Exception 	{
		byte[]			result = null;
		int				nNewLen;
		try {
			nNewLen = ( ( baSrc.length + 16 ) / 16 ) * 16;
			result = new byte[ nNewLen ];
			//
			// 0 으로 초기화
			//
			for( int i = 0; i < nNewLen; i++ ) {
				result[ i ] = 0;
			}
			
			//
			// 암호화할 데이타 복사 
			//
			System.arraycopy( baSrc, 0, result, 0, baSrc.length );
			
			DESedeKeySpec spec = new DESedeKeySpec( m_baKey );
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance( "DESEDE" );
			Key key = keyFactory.generateSecret( spec );
			Cipher cipher = Cipher.getInstance( "DESEDE/ECB/PKCS7Padding" );
			cipher.init( Cipher.ENCRYPT_MODE, key );
			//
			// 암호화
			//
			result = cipher.doFinal( result );
		} catch( Exception e ) {
			throw e;
		} finally {}
		
		return result;
	}
	
	
	/**
	 * 
	 * Method Name :
	 * @param baSrc
	 * @return
	 * @throws Exception
	 * @see unidocs.drm.cryptography.CryptoFactory#decrypt(byte[])
	 */
	public byte[] decrypt( byte[] baSrc ) throws Exception {
		byte[] result = null;
		
		try {
			DESedeKeySpec spec = new DESedeKeySpec( m_baKey );
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance( "DESEDE" );
			Key key = keyFactory.generateSecret( spec );
			Cipher cipher = Cipher.getInstance( "DESEDE/ECB/PKCS7Padding" );
			cipher.init( Cipher.DECRYPT_MODE, key );
			result = cipher.doFinal( baSrc );
		} catch( Exception e ) {
			throw e;
		} finally {}
		 
		return result;
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
				//System.out.println( buffer[ i ]);
				nValue = (int)buffer[ i ] & 0x00ff;
				if( nValue < 16 ) buf.append( "0" );
				buf.append( Integer.toHexString( nValue ).toUpperCase() );
			}
		} catch( Exception e ) {
			throw e;
		} finally {}
		
		return buf.toString();
    }
	
	public static byte[] HexStringToBinary(String hexString)throws Exception{
		if( hexString.length() % 2 > 0 )
			throw new RuntimeException("대상문자열 길이가 올바르지 않습니다");
		
		int byteLength = hexString.length() / 2;
		byte[] arrResult = new byte[ byteLength ];
		String strPart = null;
		for( int i = 0; i < byteLength; i++ ){
			strPart = hexString.substring( i * 2, i * 2 + 2 );
			arrResult[ i ] = (byte)Integer.parseInt( strPart, 16);
			//System.out.println( arrResult[ i ] );
		}//for
		
		return arrResult;
	}
	
	public static void main(String[] args) throws Exception {
		CryptoDESedeEx des = new CryptoDESedeEx();
		des.setKeyString("test111122223333test111122223333");
		System.out.println( CryptoDESedeEx.BinaryToHexString(des.encrypt("test")));
		
		
	}

} // end class

