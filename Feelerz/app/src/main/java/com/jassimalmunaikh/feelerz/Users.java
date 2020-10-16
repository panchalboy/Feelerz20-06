package com.jassimalmunaikh.feelerz;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class Users {

    private String fullname;
    private String username;
    private String imageId;
//    private String privacy;
//    private String ownerid;
//    private String userId;
//    private boolean female;

    public Users(String name, String imageId, String userName, @NonNull String fullname, @NonNull String username, @NonNull String ownerid, @NonNull String privacy) {
        this.fullname = fullname;
        this.username = username;
//        this.ownerid = ownerid;
//        this.privacy = privacy;
    }

    public Users(@NonNull String fullname, @NonNull String username, String female) {
        this.fullname = fullname;
        this.username = username;
//        this.female = female;
    }


    public Users(String s, String id, @NonNull String name, @NonNull String imageId, @NonNull String username) {
        this.fullname = name;
        this.imageId = imageId;
        this.username = username;
    }

    @NonNull
    public String getFullname() {
        return fullname;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    @NonNull
    public String getImageId() {
        return imageId;
    }

//    @NonNull
//    public String getOwnerId() {
//        return ownerid;
//    }

//    @NonNull
//    public String getprivacy() {
//        return privacy;
//    }

//    @NonNull
//    public String getUserId() {
//        return userId;
//    }

//    public boolean isFemale() {
//        return female;
//    }

    // Thanks uinames.com
//    public static List<Users> USERS = Arrays.asList(
//            new Users("Lori Rice", "lori__rice", true),
//            new Users("Karen Sandoval", "karen93", true),
//            new Users("Andrea Wagner", "andrea_86"),
//            new Users("Jerry Sanchez", "jerry-85"),
//            new Users("Elizabeth Carroll", "elizabeth-94", true),
//            new Users("Ronald Tran", "ronald_tran"),
//            new Users("Crystal Castillo", "crystal.castillo", true),
//            new Users("Sean King", "sean"),
//            new Users("Paul Aguilar", "paul.aguilar"),
//            new Users("Benjamin Gonzalez", "ben-85"),
//            new Users("Ryan Curtis", "ryan-94"),
//            new Users("Jane Willis", "jane_willis", true),
//            new Users("Diane Price", "diane__price", true),
//            new Users("Marie Elliott", "marie95", true),
//            new Users("Peter Cole", "peter_83"),
//            new Users("Donald Green", "donald-35"),
//            new Users("Frank Oliver", "frank-oliver"),
//            new Users("Doris Walters", "doris", true),
//            new Users("Jack Lynch", "jack-lynch"),
//            new Users("Ruth Patel", "patel"),
//            new Users("Donald Obrien", "obrien.donald"),
//            new Users("Joyce Wells", "jwells"),
//            new Users("Austin Keller", "keller-94"),
//            new Users("Jean Watkins", "jw", true),
//            new Users("Julio Cesar Paredes", "julio.cesar"),
//            new Users("Fabian Mercedesz", "fabian"),
//            new Users("Roma Kania", "roma.kania", true),
//            new Users("Luna Vidal", "luna-75", true),
//            new Users("Daisy Roberts", "roberts-93", true),
//            new Users("Matthew Maxton", "matthew_maxton"),
//            new Users("Claudio Guerra", "guerra.claudio"),
//            new Users("Floare Carafoli", "floare84", true),
//            new Users("Esra Yilmaz", "esra_83", true),
//            new Users("Casanda Goian", "casanda-1935", true),
//            new Users("Kyle Lawson", "kyle-law"),
//            new Users("Mathijs de Boer", "mdboer"),
//            new Users("Mitchell Sarah", "mitchell-sarah"),
//            new Users("Carolina Rotaru", "rotaru", true),
//            new Users("Joe Fernandez", "joe.fernandez"),
//            new Users("Christian Colombo", "ccolombo"),
//            new Users("Venera Steflea", "venera-91", true),
//            new Users("Helge Olsen", "holsen", true),
//            new Users("Fien Smet", "fien.smet"),
//            new Users("Hugo Aviles", "aviles"),
//            new Users("Elizabeth Montoya", "elizabeth.montoya", true),
//            new Users("Mihnea Gliga", "mihnea-75", true),
//            new Users("Gary Cook", "cook-96"),
//            new Users("Seppe Smet", "seppe_smet"),
//            new Users("Diane Lane", "diane.lane", true),
//            new Users("Sophia Ackroyd", "sophia", true),
//            new Users("Octavia Sirma", "octavia_sirma", true),
//            new Users("Ciprian Tutoveanu", "ciprian"),
//            new Users("Ida Birkeland", "birkeland-ida", true),
//            new Users("Tore Haugland", "torehaug"),
//            new Users("Denis Vaska", "denis-vaska"),
//            new Users("Milena Corbea", "corbeamilena", true),
//            new Users("Gyurkovics Letti", "gyur.letti "),
//            new Users("Oliviu Fugaru", "oliviufu"),
//            new Users("Semiha Erdem", "semi-91"),
//            new Users("Codin Ardelean", "codin.ardelean")
//    );
}
