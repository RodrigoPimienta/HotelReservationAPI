package com.revature.dto.response;

import com.revature.models.HotelRoom;

public class HotelRoomWithRoomTypeDTO {
        private int hotelRoomId;
        private int num;
        private RoomTypeDTO roomType; // Usa RoomTypeDTO

        public HotelRoomWithRoomTypeDTO() {}

        public HotelRoomWithRoomTypeDTO(HotelRoom room) {
                this.hotelRoomId = room.getHotelRoomId();
                this.num = room.getNum();
                this.roomType = new RoomTypeDTO(room.getRoomType()); // Crea RoomTypeDTO
        }

        public int getHotelRoomId() {
                return hotelRoomId;
        }

        public void setHotelRoomId(int hotelRoomId) {
                this.hotelRoomId = hotelRoomId;
        }

        public int getNum() {
                return num;
        }

        public void setNum(int num) {
                this.num = num;
        }

        public RoomTypeDTO getRoomType() {
                return roomType;
        }

        public void setRoomType(RoomTypeDTO roomType) {
                this.roomType = roomType;
        }
}