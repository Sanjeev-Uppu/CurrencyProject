import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Task1 {
    private static final String URL="";
    private static final String USERNAME="";
    private static final String PASSWORD="";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try (Connection com=DriverManager.getConnection(URL,USERNAME,PASSWORD);
             Statement stat=com.createStatement()) {

            while (true) {
                System.out.println("\nExpense Tracker Menu:");
                System.out.println("1. Add Expense");
                System.out.println("2. Edit Expense");
                System.out.println("3. Delete Expense");
                System.out.println("4. View Expenses");
                System.out.println("5. Currency Conversion");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");

                Scanner sc=new Scanner(System.in);
                int choice=sc.nextInt();

                switch (choice) {
                    case 1:
                        addExpense(com,stat);
                        break;
                    case 2:
                        editExpense(com,stat);
                        break;
                    case 3:
                        deleteExpense(com,stat);
                        break;
                    case 4:
                        viewExpenses(com,stat);
                        break;
                    case 5:
                        System.out.print("Enter the amount to exchange: ");
                        int amount=sc.nextInt();
                        System.out.print("Enter source currency (USD, EUR, GBP): ");
                        String fromCurrency=sc.next();
                        System.out.print("Enter target currency (USD, EUR, GBP): ");
                        String toCurrency=sc.next();
                        currencyConverter(fromCurrency,toCurrency,amount);
                        break;
                    case 0:
                        exit();
                        return;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addExpense(Connection com,Statement stat) {
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter description: ");
        String name=sc.nextLine();
        System.out.print("Enter amount: ");
        double amount=sc.nextDouble();

        String query="INSERT INTO management (item,price) VALUES ('"+name+"',"+amount+")";
        try {
            int rows=stat.executeUpdate(query);
            System.out.println(rows>0?"Expense added successfully!":"Error adding expense.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void editExpense(Connection com,Statement stat) {
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter the Purchase ID: ");
        int id=sc.nextInt();

        if (!recordExists(com,stat,id)) {
            System.out.println("Purchase ID does not exist.");
            return;
        }

        System.out.print("Enter new description: ");
        sc.nextLine();
        String description=sc.nextLine();
        System.out.print("Enter new amount: ");
        double amount=sc.nextDouble();

        String query="UPDATE management SET item='"+description+"',price="+amount+" WHERE purchase_id="+id;
        try {
            int rows=stat.executeUpdate(query);
            System.out.println(rows>0?"Expense updated successfully!":"Error updating expense.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteExpense(Connection com,Statement stat) {
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter the Purchase ID: ");
        int id=sc.nextInt();

        if (!recordExists(com,stat,id)) {
            System.out.println("Purchase ID does not exist.");
            return;
        }

        String query="DELETE FROM management WHERE purchase_id="+id;
        try {
            int rows=stat.executeUpdate(query);
            System.out.println(rows>0?"Expense deleted successfully!":"Error deleting expense.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewExpenses(Connection com,Statement stat) {
        String query="SELECT * FROM management";
        try {
            ResultSet rs=stat.executeQuery(query);
            while (rs.next()) {
                int ID=rs.getInt("purchase_id");
                String name=rs.getString("item");
                int price=rs.getInt("price");
                String date=rs.getTimestamp("purchase_date").toString();
                System.out.println();
                System.out.println("=================================");
                System.out.println("Your ID is: "+ID);
                System.out.println("Item name is: "+name);
                System.out.println("Price is: "+price);
                System.out.println("Date and Time: "+date);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void currencyConverter(String fromCurrency,String toCurrency,int amount) {
        Map<String,Double> conversionRates=new HashMap<>();
        conversionRates.put("USD_TO_EUR",0.85);
        conversionRates.put("EUR_TO_USD",1.18);
        conversionRates.put("USD_TO_GBP",0.75);
        conversionRates.put("GBP_TO_USD",1.33);

        String conversionKey=fromCurrency+"_TO_"+toCurrency;
        if (conversionRates.containsKey(conversionKey)) {
            System.out.println(amount*conversionRates.get(conversionKey));
        } else {
            System.out.println("Conversion rate not available.");
        }
    }

    public static void exit() {
        System.out.println("Thank you!");
    }

    public static boolean recordExists(Connection com,Statement stat,int id) {
        try {
            String query="SELECT purchase_id FROM management WHERE purchase_id="+id;
            ResultSet rs=stat.executeQuery(query);
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
