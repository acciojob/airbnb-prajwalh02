package com.driver.controllers;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class HotelManagementRepository {

    Map<String, Hotel> hotelDb = new HashMap<>();
    Map<Integer, User> userDb = new HashMap<>();
    Map<String, Booking> bookingDb = new HashMap<>();
    Map<Integer, List<Booking>> userBookingDb = new HashMap<>();

    private int maxCountOfFacility = 0;
    private String maxFacilityHotel = "";

    public String addHotel(Hotel hotel) {

        if(hotel == null) {
            return "FAILURE";
        }

        String key = hotel.getHotelName();
        if(key == null) {
            return "FAILURE";
        }
        if(hotelDb.containsKey(key)) {
            return "FAILURE";
        }

        hotelDb.put(key, hotel);

        int countOfFacilities = hotel.getFacilities().size();

        if(countOfFacilities >= maxCountOfFacility){
            if(countOfFacilities == maxCountOfFacility){
                if(hotel.getHotelName().compareTo(maxFacilityHotel)<0){
                    maxFacilityHotel = hotel.getHotelName();
                }
            }else{
                maxCountOfFacility = countOfFacilities;
                maxFacilityHotel = hotel.getHotelName();
            }
        }
        return "SUCCESS";
    }

    public Integer addUser(User user) {
        userDb.put(user.getaadharCardNo(), user);
        return user.getaadharCardNo();
    }

    public String getHotelWithMostFacilities() {
        return maxFacilityHotel;
    }

    public int bookARoom(Booking booking) {

        Hotel hotelToBeBooked = hotelDb.get(booking.getHotelName());
        if(booking.getNoOfRooms() > hotelToBeBooked.getAvailableRooms()){
            return -1;
        }
        else {
            hotelToBeBooked.setAvailableRooms(hotelToBeBooked.getAvailableRooms() - booking.getNoOfRooms());
            booking.setBookingId(String.valueOf(UUID.randomUUID()));
            booking.setAmountToBePaid(booking.getNoOfRooms() * hotelToBeBooked.getPricePerNight());
            bookingDb.put(booking.getBookingId(), booking);

            if(!userBookingDb.containsKey(booking.getBookingAadharCard())){
                userBookingDb.put(booking.getBookingAadharCard(), new ArrayList<>());
            }
            userBookingDb.get(booking.getBookingAadharCard()).add(booking);

            return booking.getAmountToBePaid();
        }
    }

    public int getBookings(Integer aadharCard) {
        return userBookingDb.get(aadharCard).size();
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {

        if(!hotelDb.containsKey(hotelName)) {
            return new Hotel();
        }

        Hotel currHotel = hotelDb.get(hotelName);

        for(Facility facility : newFacilities) {
            if(!currHotel.getFacilities().contains(facility)) {
                currHotel.getFacilities().add(facility);
            }
        }

        int countOfFacilities = currHotel.getFacilities().size();

        if(countOfFacilities >= maxCountOfFacility) {
            if(countOfFacilities == maxCountOfFacility) {
                if(currHotel.getHotelName().compareTo(maxFacilityHotel) < 0) {
                    maxFacilityHotel = currHotel.getHotelName();
                }
            }
            else {
                maxCountOfFacility = countOfFacilities;
                maxFacilityHotel = currHotel.getHotelName();
            }
        }
        return currHotel;
    }
}
