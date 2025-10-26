package com.example.roomease;


    public class RoomStatus {
        private String roomNumber;
        private String status;

        public RoomStatus(String roomNumber, String status) {
            this.roomNumber = roomNumber;
            this.status = status;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public void setRoomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
