import jdk.internal.access.JavaIOFileDescriptorAccess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import static java.util.prefs.Preferences.MAX_NAME_LENGTH;


public class NameGenerator {
    private static final char[] ALPHABET =null;
    // Структура для хранения вероятностей биграмм
    private static Map<String, Double> bigramProbabilities = new HashMap<>();

    public static void main(String[] args) {
        // Прочитать данные из файла и вычислить вероятности биграмм
        readData("names.txt");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Введите 'g' для генерации имени или 't' для вывода таблицы вероятностей биграмм:");
            String input = scanner.nextLine();

            if (input.equals("g")) {
                // Сгенерировать имя
                String name = generateName();
                System.out.println("Сгенерированное имя: " + name);
            } else if (input.equals("t")) {
                // Вывести таблицу вероятностей биграмм
                printTable();
            } else {
                System.out.println("Некорректный ввод, попробуйте еще раз.");
            }
        }
    }

    private static void printTable() {
    }

    // Функция для чтения данных из файла и вычисления вероятностей биграмм
    private static void readData(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            // Считать все строки из файла и создать список имен
            List<String> names = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                names.add(line.trim().toLowerCase());
            }

            // Вычислить вероятности биграмм
            int totalBigrams = 0;
            Map<String, Integer> bigramCounts = new HashMap<>();

            for (String name : names) {
                for (int i = 0; i < name.length() - 1; i++) {
                    String bigram = name.substring(i, i + 2);

                    // Обновить количество вхождений биграммы
                    int count = bigramCounts.getOrDefault(bigram, 0);
                    bigramCounts.put(bigram, count + 1);
                    totalBigrams++;
                }
            }

            // Вычислить вероятности биграмм
            for (Map.Entry<String, Integer> entry : bigramCounts.entrySet()) {
                String bigram = entry.getKey();
                int count = entry.getValue();
                double probability = (double) count / totalBigrams;
                bigramProbabilities.put(bigram, probability);
            }

        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    // Функция для генерации имени
    private static String generateName() {
        Random random = new Random();
        StringBuilder nameBuilder = new StringBuilder();

        // Выбрать случайную первую букву из списка биграмм, которые могут быть первой буквой имени
        List<String> startingBigrams = new ArrayList<>();
        HashMap<Object, Object> bigramCounts = null;
        for (Map.Entry<String, Map<String, Integer>> entry : bigramCounts.entrySet()) {
            if (entry.getKey().startsWith("^")) {
                startingBigrams.add(entry.getKey());
            }
        }
        String startingBigram = startingBigrams.get(random.nextInt(startingBigrams.size()));
        nameBuilder.append(startingBigram.charAt(1)); // добавляем в имя вторую букву первой биграммы

        // Генерировать следующие буквы имени, используя вероятности биграмм
        while (nameBuilder.length() < MAX_NAME_LENGTH) {
            String currentBigram = nameBuilder.substring(nameBuilder.length() - 1); // текущая биграмма, из которой будем выбирать следующую букву
            Map<String, Integer> nextLetterCounts = bigramCounts.get(currentBigram);
            if (nextLetterCounts == null) {
                break; // если мы дошли до конца имени, для которого не нашлось подходящих биграмм, прерываем генерацию
            }
            // формируем список следующих букв на основе вероятностей
            List<String> nextLetters = new ArrayList<>();
            int totalCount = 0;
            for (Map.Entry<String, Integer> entry : nextLetterCounts.entrySet()) {
                totalCount += entry.getValue();
            }
            for (Map.Entry<String, Integer> entry : nextLetterCounts.entrySet()) {
                int count = entry.getValue();
                double probability = (double) count / totalCount;
                for (int i = 0; i < probability * 100; i++) {
                    nextLetters.add(entry.getKey());
                }
            }
            if (nextLetters.isEmpty()) {
                break; // если мы дошли до конца имени, для которого не нашлось подходящих биграмм, прерываем генерацию
            }
            // выбираем случайную следующую букву и добавляем ее в имя
            String nextLetter = nextLetters.get(random.nextInt(nextLetters.size()));
            nameBuilder.append(nextLetter.charAt(1));
        }

        return nameBuilder.toString();
    }

    // Метод для вывода таблицы вероятностей биграмм
    private static void printBigramTable() {
        System.out.println("Bigram probabilities:");
        System.out.print("  ");
        for (char c : ALPHABET) {
            System.out.print(c + " ");
        }
        System.out.println();
        for (char c1 : ALPHABET) {
            System.out.print(c1 + " ");
            for (char c2 : ALPHABET) {
                String bigram = "" + c1 + c2;
                JavaIOFileDescriptorAccess bigramCounts = null;
                Map<String, Integer> nextLetterCounts = bigramCounts.get(bigram);
                if (nextLetterCounts == null) {
                    System.out.print(". ");
                }
            }
        }
    }
}
