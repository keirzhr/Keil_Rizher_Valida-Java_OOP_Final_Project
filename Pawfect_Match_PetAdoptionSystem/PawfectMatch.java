import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

abstract class Being {      //inheritance / abstraction
    private String name;
    private String uniqueId;

    public Being(String name, String uniqueId) {
        this.name = name;
        this.uniqueId = uniqueId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUniqueId() { return uniqueId; }
}

interface Authenticatable {
    boolean authenticate(String username, String password);
}

// inheritance
abstract class User implements Authenticatable {
    private String username;
    private String password;
    private String email;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public String getEmail() { return email; }

    public abstract void displayDashboard();

    @Override
    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
}

// encapsulation  / inheritance
class Pet extends Being {
    private String species;
    private int age;
    private String breed;
    private AdoptionStatus status;

    public enum AdoptionStatus {
        AVAILABLE, PENDING, ADOPTED
    }

    public Pet(String name, String uniqueId, String species, int age, String breed) {
        super(name, uniqueId);
        this.species = species;
        this.age = age;
        this.breed = breed;
        this.status = AdoptionStatus.AVAILABLE;
    }

    // Getters and setters
    public String getSpecies() { return species; }
    public int getAge() { return age; }
    public String getBreed() { return breed; }
    public AdoptionStatus getStatus() { return status; }
    public void setStatus(AdoptionStatus status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Name: %s | Species: %s | Age: %d | Breed: %s | Status: %s", 
            getName(), species, age, breed, status);
    }
}

// Adoption Application class
class AdoptionApplication {
    private User applicant;
    private Pet pet;
    private LocalDate applicationDate;
    private ApplicationStatus status;

    public enum ApplicationStatus {
        PENDING, APPROVED, REJECTED
    }

    public AdoptionApplication(User applicant, Pet pet) {
        this.applicant = applicant;
        this.pet = pet;
        this.applicationDate = LocalDate.now();
        this.status = ApplicationStatus.PENDING;
    }

    // Getters
    public User getApplicant() { return applicant; }
    public Pet getPet() { return pet; }
    public LocalDate getApplicationDate() { return applicationDate; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
}

class RegularUser extends User {
    private List<AdoptionApplication> applications;

    public RegularUser(String username, String password, String email) {
        super(username, password, email);
        this.applications = new ArrayList<>();
    }

    public void applyForAdoption(Pet pet) {
        AdoptionApplication application = new AdoptionApplication(this, pet);
        applications.add(application);
    }

    public List<AdoptionApplication> getApplications() {
        return applications;
    }

    @Override // POLYMORPHISM
    public void displayDashboard() {
        System.out.println("\n╔══════════════════════════════════╗");  
        System.out.println("║          User Dashboard          ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.println("Welcome, " + getUsername());
        System.out.println("Email: " + getEmail());
        System.out.println("Adoption Applications:");
        for (AdoptionApplication app : applications) {
            System.out.println(app.getPet().getName() + " - " + app.getStatus());
        }
    }
}

class AdminUser extends User {
    private List<Pet> pets;
    private List<AdoptionApplication> allApplications;

    public AdminUser(String username, String password, String email) {
        super(username, password, email);
        this.pets = new ArrayList<>();
        this.allApplications = new ArrayList<>();
    }

    public void addPet(Pet pet) {
        pets.add(pet);
    }

    public void addApplication(AdoptionApplication application) {
        allApplications.add(application);
    }

    public void reviewApplications() {
        System.out.println("\n╔══════════════════════════════════╗");  
        System.out.println("║   Pending Adoption Applications  ║");
        System.out.println("╚══════════════════════════════════╝");
        if (allApplications.isEmpty()) {
            System.out.println("\n    == No pending applications == ");
            return;
        }
        
        for (AdoptionApplication app : allApplications) {
            if (app.getStatus() == AdoptionApplication.ApplicationStatus.PENDING) {
                String applicant = String.format("%-21s", app.getApplicant().getUsername());
                String petName = String.format("%-21s", app.getPet().getName());
                String species = String.format("%-21s", app.getPet().getSpecies());
                String date = app.getApplicationDate().format(DateTimeFormatter.ISO_DATE);
        
                System.out.println("╔══════════════════════════════════╗");
                System.out.println("║ Applicant: " + applicant + " ║");
                System.out.println("║ Pet:       " + petName + " ║");
                System.out.println("║ Species:   " + species + " ║");
                System.out.println("║ Application Date: " + date + "     ║");
                System.out.println("╚══════════════════════════════════╝");
            }
        }        
    }

    @Override
    public void displayDashboard() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║         Admin Dashboard          ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.println("Welcome, " + getUsername());
        System.out.println("Pets in System: " + pets.size());
        System.out.println("Total Applications: " + allApplications.size());
    }
}

// Main Application Class
public class PawfectMatch {
    private static Scanner scanner = new Scanner(System.in);
    private static List<RegularUser> regularUsers = new ArrayList<>();
    private static List<AdminUser> adminUsers = new ArrayList<>();
    private static List<Pet> availablePets = new ArrayList<>();
    private static List<AdoptionApplication> applications = new ArrayList<>();

    public static void main(String[] args) {
        // Seed some initial data
        seedInitialData();

        while (true) {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║        - PAWFECT MATCH -         ║");
            System.out.println("║      A Pet Adoption  System      ║");
            System.out.println("╚══════════════════════════════════╝");    
            System.out.println("1. Register (Create an Account)");
            System.out.println("2. User Login");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1: registerUser(); break;
                    case 2: userLogin(); break;
                    case 3: adminLogin(); break;
                    case 4: 
                        System.out.println("\n\n=========================================================");
                        System.out.println("=== Thank you for using Pet Adoption System. Goodbye! ===");
                        System.out.println("=========================================================\n\n");
                        return;
                    default: 
                        System.out.println("\n!! Invalid choice. Please try again !!\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("\n!! Invalid input. Please enter a number !!\n");
                scanner.nextLine(); 
            }
        }
    }

    private static void seedInitialData() {
        // Create an admin user
        AdminUser admin = new AdminUser("keil", "keilpogi123", "keil@gmail.com");
        adminUsers.add(admin);

        // Create some pets
        Pet dog1 = new Pet("Bantay", "D001", "Dog", 3, "Aspin");
        Pet cat1 = new Pet("Mingming", "C001", "Cat", 2, "Siamese");
        Pet dog2 = new Pet("Max", "D002", "Dog", 4, "Labrador Retriever");
        Pet cat2 = new Pet("Nina", "C002", "Cat", 3, "Persian");
        Pet dog3 = new Pet("Rex", "D003", "Dog", 5, "German Shepherd");
        
        availablePets.add(dog1);
        availablePets.add(cat1);
        availablePets.add(dog2);
        availablePets.add(cat2);
        availablePets.add(dog3);
        
        admin.addPet(dog1);
        admin.addPet(cat1);
        admin.addPet(dog2);
        admin.addPet(cat2);
        admin.addPet(dog3);
        
    }

    private static void registerUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        RegularUser newUser = new RegularUser(username, password, email);
        regularUsers.add(newUser);
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║     Registration successful!     ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    private static void userLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        for (RegularUser user : regularUsers) {
            if (user.authenticate(username, password)) {
                userMenu(user);
                return;
            }
        }
        System.out.println("\n!! Login failed. Invalid credentials !!\n");
    }

    private static void userMenu(RegularUser user) {
        while (true) {
            System.out.println("\n╔══════════════════════════════════╗");   
            System.out.println("║            Main Menu             ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.println("1. View All Pets");
            System.out.println("2. Search Pets by Filters");
            System.out.println("3. Apply for Adoption");
            System.out.println("4. View Your Adoption History");
            System.out.println("5. Log Out");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1: viewAllPets(); break;
                    case 2: searchPets(); break;
                    case 3: applyForAdoption(user); break;
                    case 4: user.displayDashboard(); break;
                    case 5: return;
                    default: 
                        System.out.println("\n!! Invalid choice. Please try again !!\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("\n!! Invalid input. Please enter a number !!\n");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    private static void adminLogin() {
        System.out.print("Enter admin username: ");
        String username = scanner.nextLine();
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();

        for (AdminUser admin : adminUsers) {
            if (admin.authenticate(username, password)) {
                adminMenu(admin);
                return;
            }
        }
        System.out.println("\n!! Login failed. Invalid credentials !!\n");
    }

    private static void adminMenu(AdminUser admin) {
        while (true) {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║           Admin Portal           ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.println("1. View All Pet Applications");
            System.out.println("2. Approve/Reject Adoption Applications");
            System.out.println("3. View All Pets");
            System.out.println("4. Add New Pet");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
    
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
    
                switch (choice) {
                    case 1: admin.reviewApplications(); break;
                    case 2: approveRejectApplications(admin); break;
                    case 3: viewAllPets(); break;
                    case 4: addNewPet(admin); break;
                    case 5: return;
                    default: 
                        System.out.println("\n!! Invalid choice. Please try again !!\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("\n!! Invalid input. Please enter a number !!\n");
                scanner.nextLine();
            }
        }
    }
    
    private static void addNewPet(AdminUser admin) {
        System.out.println("╔══════════════════════════════════╗"); 
        System.out.println("║            Add New Pet           ║");
        System.out.println("╚══════════════════════════════════╝");
        
        // Generate a unique ID for the pet
        String uniqueId = "P" + (availablePets.size() + 1);
        
        // Get pet details from admin
        System.out.print("Enter Pet Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Species (Dog/Cat/etc.): ");
        String species = scanner.nextLine();
        
        int age;
        while (true) {
            try {
                System.out.print("Enter Pet Age: ");
                age = scanner.nextInt();
                scanner.nextLine();
                if (age >= 0) break;
                System.out.println("\n!! Age must be a non-negative number !!\n");
            } catch (InputMismatchException e) {
                System.out.println("\n!! Invalid input. Please enter a number !!\n");
                scanner.nextLine();
            }
        }
        
        System.out.print("Enter Breed: ");
        String breed = scanner.nextLine();
        
        // Create new pet
        Pet newPet = new Pet(name, uniqueId, species, age, breed);
        
        // Add to available pets list
        availablePets.add(newPet);
        
        // Add to admin's pet list
        admin.addPet(newPet);
        
        System.out.println("\n=================================");
        System.out.println("       Pet Added Successfully!");
        System.out.println("=================================");
        System.out.println("Pet Details:");
        System.out.println("Name: " + name);
        System.out.println("Species: " + species);
        System.out.println("Age: " + age);
        System.out.println("Breed: " + breed);
        System.out.println("Unique ID: " + uniqueId);
    }

    private static void viewAllPets() {
        System.out.println("\n╔══════════════════════════════════╗"); 
        System.out.println("║       === Available Pets ===     ║");
        System.out.println("╚══════════════════════════════════╝");
        for (Pet pet : availablePets) {
            if (pet.getStatus() == Pet.AdoptionStatus.AVAILABLE) {
                System.out.println(pet);
            }
        }
    }

    private static void searchPets() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║        === Search Pets ===       ║");
        System.out.println("╚══════════════════════════════════╝");
        
        System.out.print("Enter species (or press Enter to skip): ");
        String species = scanner.nextLine().trim();
        
        int maxAge;
        while (true) {
            System.out.print("Enter max age (or 0 to skip): ");
            try {
                maxAge = scanner.nextInt();
                scanner.nextLine(); // consume newline
                break;
            } catch (InputMismatchException e) {
                System.out.println("\n!! Invalid input. Please enter a number !!\n");
                scanner.nextLine(); // clear invalid input
            }
        }
    
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║       === Search Results ===     ║");
        System.out.println("╚══════════════════════════════════╝");
        
        boolean foundResults = false;
        
        for (Pet pet : availablePets) {
            // Check for available pets
            if (pet.getStatus() == Pet.AdoptionStatus.AVAILABLE) {
                // Check species (ignore case, allow partial matches)
                boolean speciesMatch = species.isEmpty() || 
                    pet.getSpecies().toLowerCase().contains(species.toLowerCase());
                
                // Check age
                boolean ageMatch = maxAge == 0 || pet.getAge() <= maxAge;
                
                if (speciesMatch && ageMatch) {
                    System.out.println(pet);
                    foundResults = true;
                }
            }
        }
        
        if (!foundResults) {
            System.out.println("No pets found matching your search criteria.");
        }
    }

    private static void applyForAdoption(RegularUser user) {
        viewAllPets();
        System.out.print("\nEnter the name of the pet you want to adopt: ");
        String petName = scanner.nextLine();
    
        for (Pet pet : availablePets) {
            if (pet.getName().equalsIgnoreCase(petName) && 
                pet.getStatus() == Pet.AdoptionStatus.AVAILABLE) {
                // Create the adoption application
                AdoptionApplication application = new AdoptionApplication(user, pet);
                
                // Add to the central applications list
                applications.add(application);
                
                // Add to the user's personal applications
                user.applyForAdoption(pet);

                if (!adminUsers.isEmpty()) {
                    AdminUser admin = adminUsers.get(0);
                    admin.addApplication(application);
                }
                System.out.println("\n=======================================================");
                System.out.println("Adoption application submitted for " + pet.getName());
                System.out.println("=======================================================");
                return;
            }
        }
        System.out.println("\n!! Pet not found or not available for adoption !!\n");
    }

    private static void approveRejectApplications(AdminUser admin) {
        admin.reviewApplications();

        // Check if there are any pending applications
        boolean hasPendingApplications = applications.stream()
            .anyMatch(app -> app.getStatus() == AdoptionApplication.ApplicationStatus.PENDING);
        
        if (!hasPendingApplications) {
            System.out.println("\n!! No pending applications to review !!\n");
            return;
        }

        System.out.println("\n===== Pending Applications =====");
        applications.stream()
            .filter(app -> app.getStatus() == AdoptionApplication.ApplicationStatus.PENDING)
            .forEach(app -> System.out.println(
                "Applicant: " + app.getApplicant().getUsername() + 
                " | Pet: " + app.getPet().getName() + 
                " | Date: " + app.getApplicationDate()
            ));

        System.out.print("\nEnter the pet name or applicant username to review their application: ");
        String searchTerm = scanner.nextLine().trim();

        List<AdoptionApplication> matchingApplications = applications.stream()
            .filter(app -> app.getStatus() == AdoptionApplication.ApplicationStatus.PENDING &&
                        (app.getPet().getName().equalsIgnoreCase(searchTerm) ||
                            app.getApplicant().getUsername().equalsIgnoreCase(searchTerm)))
            .collect(Collectors.toList());

        if (matchingApplications.isEmpty()) {
            System.out.println("\n!! No pending applications found matching your search !!\n");
            return;
        }

        // If multiple matching applications, let admin choose
        AdoptionApplication selectedApplication;
        if (matchingApplications.size() > 1) {
            System.out.println("\nMultiple matching applications found:");
            for (int i = 0; i < matchingApplications.size(); i++) {
                AdoptionApplication app = matchingApplications.get(i);
                System.out.println((i + 1) + ". Applicant: " + app.getApplicant().getUsername() + 
                                " | Pet: " + app.getPet().getName());
            }
            
            int choice;
            while (true) {
                try {
                    System.out.print("Select the application number: ");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    
                    if (choice > 0 && choice <= matchingApplications.size()) {
                        selectedApplication = matchingApplications.get(choice - 1);
                        break;
                    } else {
                        System.out.println("\n!! Invalid selection. Please try again !!\n");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("\n!! Invalid input. Please enter a number !!\n");
                    scanner.nextLine();
                }
            }
        } else {
            selectedApplication = matchingApplications.get(0);
        }

        // Display detailed application info
        System.out.println("\n=============================================");
        System.out.println("Application Details:");
        System.out.println("Applicant: " + selectedApplication.getApplicant().getUsername());
        System.out.println("Pet: " + selectedApplication.getPet().getName());
        System.out.println("Species: " + selectedApplication.getPet().getSpecies());
        System.out.println("Application Date: " + 
            selectedApplication.getApplicationDate().format(DateTimeFormatter.ISO_DATE));
        
        // Approval/Rejection process
        System.out.println("\n1. Approve");
        System.out.println("2. Reject");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                // Approve application
                selectedApplication.setStatus(AdoptionApplication.ApplicationStatus.APPROVED);
                selectedApplication.getPet().setStatus(Pet.AdoptionStatus.ADOPTED);
                System.out.println("\n==============================================");
                System.out.println("Application APPROVED for " + selectedApplication.getApplicant().getUsername() + 
                                " - Pet: " + selectedApplication.getPet().getName());
                System.out.println("==============================================");
            } else if (choice == 2) {
                // Reject application
                selectedApplication.setStatus(AdoptionApplication.ApplicationStatus.REJECTED);
                System.out.println("\n==============================================");
                System.out.println("Application REJECTED for " + selectedApplication.getApplicant().getUsername() + 
                                " - Pet: " + selectedApplication.getPet().getName());
                System.out.println("==============================================");
            } else {
                System.out.println("\n!! Invalid choice. No action taken !!\n");
                }
        }catch (InputMismatchException e) {
            System.out.println("\n!! Invalid input. Please enter a number !!\n");
            scanner.nextLine();
        }
    }
}
