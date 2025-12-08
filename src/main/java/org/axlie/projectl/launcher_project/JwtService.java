package org.axlie.projectl.launcher_project;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    //used for generation secret key for podpisi jwt(json web token)
    //key is kryptographic key and used for podpisi i proverci jwt
    //hs256 is simetrichniy algorithym kotoriy ispolzuetsja i dla podpisi i dlja proverki
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    //zadaem time of expiration(istechenija)
    private static final long TOKEN_EXPIRATION_SHORT = 12 * 60 * 60 * 1000; //12 hours
    private static final long TOKEN_EXPIRATION_LONG = 7 * 24 * 60 * 60 * 1000;// 7 days
    //method dlja generacii
    public String generateToken(String username, boolean longExpiration) {
        //this is ternarniy operator(uslovie ? if true : if false) vibor zhizni tokena
        long expiration = longExpiration ? TOKEN_EXPIRATION_LONG : TOKEN_EXPIRATION_SHORT;
        //jwts builder builds the token
        return Jwts.builder()
                //zadaem komu prenadlezit token
                .setSubject(username)
                //vremja sozdanija
                .setIssuedAt(new Date())
                //vramja zizni
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                //podpis
                .signWith(key)
                //convert to stroku
                .compact();
    }
    //method validacii
    public String validateToken(String token) {
        try {
            //create parcer of jwts парсинг это разбор токена на состовляющии
            return Jwts.parserBuilder()
                    //proverka ne bil li key poddelan
                    .setSigningKey(key)
                    //builds parcer
                    .build()
                    //parsit jwt stroku and check podpis
                    .parseClaimsJws(token)
                    //izvlecaet data kotoraja bila zalozena pri sozdanii tokena
                    .getBody()
                    //izvlecaet pole gde hranitsja name or id of user
                    .getSubject();
            //lovim exception dlja opredelenija token exp or invalid
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired");
            return null;
        } catch (JwtException e) {
            System.out.println("Token invalid");
            return null;
        }
    }
}

