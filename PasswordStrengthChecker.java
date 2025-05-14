import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PasswordStrengthChecker {
	 public static HashSet<String> loadWeakPasswords(String filename) {
        HashSet<String> weakPasswords = new HashSet<>();
        try (Scanner fileScanner = new Scanner(new java.io.File(filename))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    weakPasswords.add(line.toLowerCase());
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur : impossible de charger le fichier de mots de passe faibles.");
        }
        return weakPasswords;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HashSet<String> weakPasswords = loadWeakPasswords("weak_passwords.txt");
        String continuer;

        do {
            System.out.println("Enter a password to check its strength:");
            String password = scanner.nextLine();

            boolean isWeak = weakPasswords.contains(password.toLowerCase());
            if (isWeak) {
                System.out.println(" Le mot de passe est dans la liste des mots de passe faibles !");
            }

            checkPwnedPassword(password);

        PasswordFeedback result = evaluatePasswordStrength(password);

        System.out.println("\nPassword Strength Feedback:");
        for (String comment : result.feedback) {
            System.out.println("- " + comment);
        }

        System.out.println("Normalized Score: " + result.normalizedScore + "/100");
        System.out.println("Overall password strength: " + result.strengthCategory);

        System.out.print("Évaluation visuelle : ");
        int stars = (int)(result.normalizedScore / 20);
        for (int i = 0; i < stars; i++) {
            System.out.print("*");
        }
        System.out.println();

        logPasswordAttempt(password, result.normalizedScore, result.strengthCategory);

        System.out.print("Voulez-vous tester un autre mot de passe ? (oui/non) : ");
        continuer = scanner.nextLine().trim().toLowerCase();
        System.out.println();

    } while (continuer.equals("oui"));

    System.out.println("Merci d'avoir utilisé le vérificateur de mot de passe !");
}

	public static PasswordFeedback evaluatePasswordStrength(String password) {
		int maxScore = 100;
		double score = 0.0;
		HashSet<String> feedback = new HashSet<>();

		// Length factor (weighted 30%)
		int length = password.length();
		double lengthScore;
		if (length >= 12) {
			lengthScore = 30.0; // Full marks for excellent length
			feedback.add("Password length is excellent.");
		} else if (length >= 8) {
			lengthScore = 20.0; // Partial marks for acceptable length
			feedback.add("Password length is acceptable.");
		} else {
			lengthScore = 10.0; // Low score for weak length
			feedback.add("Password is too short. Minimum length is 8 characters.");
		}
		score += lengthScore;

		// Complexity factor (weighted 50%)
		double complexityScore = 0.0;
		if (Pattern.compile("[a-z]").matcher(password).find()) {
			complexityScore += 12.5; // Partial weight for lowercase
		} else {
			feedback.add("Password should include at least one lowercase letter.");
		}

		if (Pattern.compile("[A-Z]").matcher(password).find()) {
			complexityScore += 12.5; // Partial weight for uppercase
		} else {
			feedback.add("Password should include at least one uppercase letter.");
		}

		if (Pattern.compile("[0-9]").matcher(password).find()) {
			complexityScore += 12.5; // Partial weight for digits
		} else {
			feedback.add("Password should include at least one digit.");
		}

		if (Pattern.compile("[@$!%*?&]").matcher(password).find()) {
			complexityScore += 12.5; // Partial weight for special characters
		} else {
			feedback.add("Password should include at least one special character (@$!%*?&).");
		}
		score += complexityScore;

		// Uniqueness factor (weighted 20%)
		HashSet<Character> uniqueChars = new HashSet<>();
		for (char c : password.toCharArray()) {
			uniqueChars.add(c);
		}
		double uniquenessRatio = (double) uniqueChars.size() / length;
		double uniquenessScore;
		if (uniquenessRatio > 0.7) {
			uniquenessScore = 20.0; // Full marks for good uniqueness
			feedback.add("Password has a good level of uniqueness.");
		} else {
			uniquenessScore = 10.0; // Partial marks for weak uniqueness
			feedback.add("Password could be more unique. Avoid using repetitive characters.");
		}
		score += uniquenessScore;

		// Normalized Score and Strength Category
		double normalizedScore = Math.min(score, maxScore); // Cap the score at 100
		String strengthCategory = determineStrengthCategory(normalizedScore);

		return new PasswordFeedback(normalizedScore, feedback, strengthCategory);
	}

	private static String determineStrengthCategory(double score) {
		if (score >= 80) {
			return "Strong";
		} else if (score >= 50) {
			return "Moderate";
		} else {
			return "Weak";
		}
	}
	public static void logPasswordAttempt(String password, double score, String category) {
    try (FileWriter writer = new FileWriter("password_log.txt", true)) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);
        
        String maskedPassword = password.replaceAll(".", "*");
        
        writer.write("[" + timestamp + "] Mot de passe testé : " + maskedPassword +
                     " | Score : " + score + "/100 | Niveau : " + category + "\n");

        // Confirmation affichée dans la console :
        System.out.println(" Tentative enregistrée dans password_log.txt");
        
    } catch (IOException e) {
        System.out.println(" Erreur lors de l'écriture dans le fichier de log.");
    }
}
public static void checkPwnedPassword(String password) {
        try {
            String sha1 = sha1Hex(password).toUpperCase();
            String prefix = sha1.substring(0, 5);
            String suffix = sha1.substring(5);

            java.net.URL url = new java.net.URL("https://api.pwnedpasswords.com/range/" + prefix);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            java.io.BufferedReader in = new java.io.BufferedReader(
                new java.io.InputStreamReader(connection.getInputStream()));
            String inputLine;
            boolean found = false;

            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith(suffix)) {
                    String count = inputLine.split(":")[1];
                    System.out.println(" Ce mot de passe a été compromis " + count + " fois dans des fuites de données !");
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println(" Ce mot de passe n'apparaît pas dans les fuites connues.");
            }

            in.close();
        } catch (Exception e) {
            System.out.println("Erreur lors de la vérification avec l'API HaveIBeenPwned.");
        }
    }

    private static String sha1Hex(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}

class PasswordFeedback {
	double normalizedScore;
	HashSet<String> feedback;
	String strengthCategory;

	public PasswordFeedback(double normalizedScore, HashSet<String> feedback, String strengthCategory) {
		this.normalizedScore = normalizedScore;
		this.feedback = feedback;
		this.strengthCategory = strengthCategory;
	}
}
