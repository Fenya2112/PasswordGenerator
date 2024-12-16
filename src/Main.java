import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

class PasswordGenerator
{
    private final Random random = new Random();

    // Метод для генерации пароля
    public String generatePassword(int length, String validChars)
    {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            int randomIndex = random.nextInt(validChars.length());
            password.append(validChars.charAt(randomIndex));
        }
        return password.toString();
    }
}

class CharacterSetManager
{
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:',.<>?";
    private static final String CYRILLIC_LOWER = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    private static final String CYRILLIC_UPPER = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";

    // Метод для создания строки допустимых символов
    public String buildValidCharacters(boolean useLowercase, boolean useUppercase, boolean useDigits, boolean useSpecialChars, boolean useCyrillicLower, boolean useCyrillicUpper)
    {
        StringBuilder validChars = new StringBuilder();
        if (useLowercase) validChars.append(LOWERCASE);
        if (useUppercase) validChars.append(UPPERCASE);
        if (useDigits) validChars.append(DIGITS);
        if (useSpecialChars) validChars.append(SPECIAL_CHARS);
        if (useCyrillicLower) validChars.append(CYRILLIC_LOWER);
        if (useCyrillicUpper) validChars.append(CYRILLIC_UPPER);

        if (validChars.length() == 0)
        {
            throw new IllegalArgumentException("Должен быть выбран хотя бы один набор символов");
        }

        return validChars.toString();
    }
}

class FileHandler
{
    // Метод для записи строки в файл (перезапись)
    public void writeToFile(String fileName, String content) throws IOException
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName)))
        {
            writer.write(content);
        }
    }
}

class UserInputHandler
{
    // Метод для получения выбора пользователя
    public boolean getUserChoice(Scanner scanner, String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("да"))
            {
                return true;
            } else if (input.equals("нет"))
            {
                return false;
            } else
            {
                System.out.println("Ошибка: введите 'да' или 'нет'.");
            }
        }
    }
}

class PerformanceTimer
{
    // Метод для измерения времени выполнения задачи
    public long measureExecutionTime(Runnable task)
    {
        long startTime = System.nanoTime();
        task.run();
        long endTime = System.nanoTime();
        return TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
    }
}

public class Main
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        PasswordGenerator generator = new PasswordGenerator();
        CharacterSetManager characterSetManager = new CharacterSetManager();
        FileHandler fileHandler = new FileHandler();
        UserInputHandler userInputHandler = new UserInputHandler();
        PerformanceTimer performanceTimer = new PerformanceTimer();

        String outputFileName = "generated_passwords.txt";

        try
        {
            // Получение параметров от пользователя
            boolean[] userChoices = getUserChoices(scanner, userInputHandler);

            // Генерация строки допустимых символов
            String validChars = characterSetManager.buildValidCharacters(
                    userChoices[0], userChoices[1], userChoices[2], userChoices[3], userChoices[4], userChoices[5]
            );

            // Генерация паролей и запись в файл
            generateAndSavePasswords(generator, fileHandler, performanceTimer, validChars, outputFileName);

        } catch (IllegalArgumentException e)
        {
            System.err.println("Ошибка: " + e.getMessage());
        } finally
        {
            scanner.close();
        }
    }

    // Метод для получения параметров от пользователя
    private static boolean[] getUserChoices(Scanner scanner, UserInputHandler userInputHandler)
    {
        boolean[] choices = new boolean[6];
        choices[0] = userInputHandler.getUserChoice(scanner, "Использовать латиницу (строчные буквы)? (да/нет): ");
        choices[1] = userInputHandler.getUserChoice(scanner, "Использовать латиницу (заглавные буквы)? (да/нет): ");
        choices[2] = userInputHandler.getUserChoice(scanner, "Использовать цифры? (да/нет): ");
        choices[3] = userInputHandler.getUserChoice(scanner, "Использовать спецсимволы? (да/нет): ");
        choices[4] = userInputHandler.getUserChoice(scanner, "Использовать кириллицу (строчные буквы)? (да/нет): ");
        choices[5] = userInputHandler.getUserChoice(scanner, "Использовать кириллицу (заглавные буквы)? (да/нет): ");
        return choices;
    }

    // Метод для генерации паролей и записи в файл
    private static void generateAndSavePasswords(PasswordGenerator generator, FileHandler fileHandler, PerformanceTimer performanceTimer, String validChars, String outputFileName)
    {
        int[] lengths = {10_000, 20_000, 30_000, 40_000, 50_000, 60_000, 70_000, 80_000, 90_000, 100_000,
                150_000, 200_000, 300_000, 400_000, 500_000, 600_000, 700_000, 800_000, 900_000, 1_000_000};

        StringBuilder fileContent = new StringBuilder(); // Сбор всех данных в одну строку

        for (int length : lengths)
        {
            long duration = performanceTimer.measureExecutionTime(() ->
            {
                String password = generator.generatePassword(length, validChars);
                // Добавление пароля в строку
                fileContent.append("--------------------------------------------------\n");
                fileContent.append("Пароль длиной ").append(length).append(":\n");
                fileContent.append(password).append("\n");
                fileContent.append("--------------------------------------------------\n");
            });
            System.out.println("Сгенерирован пароль длиной " + length + " за " + duration + " мс");
        }

        // Запись данных в файл
        try
        {
            fileHandler.writeToFile(outputFileName, fileContent.toString());
        } catch (IOException e)
        {
            System.err.println("Ошибка записи в файл: " + e.getMessage());
        }
    }
}