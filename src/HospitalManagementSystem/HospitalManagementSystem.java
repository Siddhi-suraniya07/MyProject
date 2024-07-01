package HospitalManagementSystem;

import java.sql.SQLException;
import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3307/hospital";
    private static final String username = "root";
    private static final String password = "siddhi@07";
    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);
            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patient");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointments");
                System.out.println("5. Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();
                switch(choice){
                    case 1:
//                        Add patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
//                        View patient
                        patient.viewPatient();
                        System.out.println();
                        break;
                    case 3:
//                        view doctors
                        doctor.viewDoctor();
                        System.out.println();
                        break;
                    case 4:
//                        book appointment
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        System.out.println("thank you for using Hospital mnanagement system");
                        return;
                    default:
                        System.out.println("enter valid choice:");
                        break;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public static  void bookAppointment(Patient patient , Doctor doctor,Connection connection,Scanner scanner){
        System.out.println("enter patient id:");
        int patientId = scanner.nextInt();
        System.out.println("enter doctor id:");
        int doctorId = scanner.nextInt();
        System.out.println("enter appointment date (YYYY-MM-DD) :");
        String appointmentDate = scanner.next();
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            if(checkDoctorAvailability(doctorId,appointmentDate,connection)){
                String appointmentQuery = "INSERT INTO appointment(P_id,D_id,appointment_date) VALUES(?,?,?)";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,patientId);
                    preparedStatement.setInt(2,doctorId);
                    preparedStatement.setString(3,appointmentDate);
                    int rowsAffected =  preparedStatement.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Appointment Booked Successfully");
                    }else{
                        System.out.println("Appointment Booking Failed");
                    }


                }catch(SQLException e){
                    e.printStackTrace();
                }

            }else{
                System.out.println("doctor is not available");
            }

        }else{
            System.out.println("either Patient or doctor does not exist");
        }

    }
    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate,Connection connection){
        String query = "SELECT COUNT(*) FROM appointment WHERE D_id=? AND appointment_date=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count == 0){
                    return true;
                }else{
                    return false;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;

    }
}
