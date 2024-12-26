package com.coderdream.util.chatgpt;

import com.coderdream.entity.Model;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ModelParser {

  public static void main(String[] args) {
    // 定义文件路径
    String inputFilePath = "src/main/resources/models_raw.txt";
    String outputFilePath = "src/main/resources/models.txt";

    // 读取并解析数据
//    List<Model> models1 = ModelParser.parseModels(inputFilePath);
    List<Model> models2 = ModelParser.parseModelsFromFile();
    for (Model model : models2) {
      System.out.println(model);
    }

    // 将解析后的数据写入文件
  //  writeModelsToFile(models, outputFilePath);
  }

  /**
   * 从指定文件读取原始数据并解析为 Model 对象列表
   *
   * @return 解析后的 Model 对象列表
   */
  public static List<Model> parseModels() {
    // 定义文件路径
    String inputFilePath = "src/main/resources/models_raw.txt";

    return parseModels(inputFilePath);
  }

  /**
   * 从指定文件读取原始数据并解析为 Model 对象列表
   *
   * @param inputFilePath 输入文件路径
   * @return 解析后的 Model 对象列表
   */
  public static List<Model> parseModels(String inputFilePath) {
    List<Model> models = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(
      new FileReader(inputFilePath))) {
      String line;
      // 逐行读取文件内容
      List<String> recordLines = new ArrayList<>();
      while ((line = reader.readLine()) != null) {
        // 过滤掉空行
        if (line.trim().isEmpty()) {
          continue;
        }

        recordLines.add(line.trim());

        // 每6行构成一条记录
        if (recordLines.size() == 6) {
          Model model = new Model();
          model.setModelName(recordLines.get(0));
          model.setModelId(recordLines.get(1));
          model.setInputCost(recordLines.get(2));
          model.setOutputCost(recordLines.get(3));
          model.setContextTokens(recordLines.get(4));
          model.setModeration(recordLines.get(5));

          models.add(model);

          // 清空缓存的行数据，准备处理下一条记录
          recordLines.clear();
        }
      }

      // 处理不完整的数据行（如果有的话，当前方法假设每条记录都包含6行）
      if (!recordLines.isEmpty()) {
        System.out.println(
          "警告：输入数据行数不为6的倍数，最后一条记录可能不完整");
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    return models;
  }

  /**
   * 从 resources 文件夹中的指定文件读取数据并解析为 Model 对象列表
   *
   * @return Model 对象列表
   */
  public static List<Model> parseModelsFromFile() {
    String inputFilePath = "src/main/resources/models.txt";
    return parseModelsFromFile(inputFilePath);
  }

  /**
   * 从 resources 文件夹中的指定文件读取数据并解析为 Model 对象列表
   *
   * @param fileName 文件名
   * @return Model 对象列表
   */
  public static List<Model> parseModelsFromFile(String fileName) {
    List<Model> models = new ArrayList<>();

    List<String> strings = null;
    try {
      strings = Files.readAllLines(Paths.get(fileName));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    for (String string : strings) {
      models.add(parseModel(string));
    }

//    // 使用类加载器加载资源文件
//    try (InputStream inputStream = ModelParser.class.getClassLoader()
//      .getResourceAsStream(fileName);
//      BufferedReader reader = new BufferedReader(
//        new InputStreamReader(inputStream))) {
//
//      String line;
//      while ((line = reader.readLine()) != null) {
//        // 解析每一行并添加到列表
//        models.add(parseModel(line));
//      }
//    } catch (IOException | NullPointerException e) {
//      System.err.println("无法读取文件: " + fileName);
//      e.printStackTrace();
//    }

    return models;
  }

  /**
   * 将Tab分隔的字符串解析为Model对象
   *
   * @param input 输入字符串
   * @return 解析后的Model对象
   */
  private static Model parseModel(String input) {
    String[] parts = input.split("\t");

    if (parts.length < 6) {
      throw new IllegalArgumentException(
        "输入字符串格式错误，无法解析为Model对象: " + input);
    }

    Model model = new Model();
    model.setModelName(parts[0].trim());
    model.setModelId(parts[1].trim());
    model.setInputCost(parts[2].trim());
    model.setOutputCost(parts[3].trim());
    model.setContextTokens(parts[4].trim());
    model.setModeration(parts[5].trim());

    return model;
  }

  /**
   * 从指定文件读取原始数据并解析为 Model 对象列表
   *
   * @param inputFilePath 输入文件路径
   * @return 解析后的 Model 对象列表
   */
  public static List<Model> parseModels2(String inputFilePath) {
    List<Model> models = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(
      new FileReader(inputFilePath))) {
      String line;
      // 逐行读取文件内容
      while ((line = reader.readLine()) != null) {
        // 过滤掉空行
        if (line.trim().isEmpty()) {
          continue;
        }

        // 将每条记录解析成 6 行数据
        String[] lines = line.split("\n");

        for (int i = 0; i < lines.length; i += 6) {
          if (i + 5 < lines.length) {
            Model model = new Model();
            model.setModelName(lines[i].trim());
            model.setModelId(lines[i + 1].trim());
            model.setInputCost(lines[i + 2].trim());
            model.setOutputCost(lines[i + 3].trim());
            model.setContextTokens(lines[i + 4].trim());
            model.setModeration(lines[i + 5].trim());

            models.add(model);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return models;
  }

  /**
   * 将 Model 对象列表写入到指定文件，属性之间用 Tab (\t) 分隔
   *
   * @param models         Model 对象列表
   * @param outputFilePath 输出文件路径
   */
  public static void writeModelsToFile(List<Model> models,
    String outputFilePath) {
    try (BufferedWriter writer = new BufferedWriter(
      new FileWriter(outputFilePath))) {
      // 写入表头
      writer.write(
        "Model Name\tModel ID\tInput Cost\tOutput Cost\tContext Tokens\tModeration\n");

      // 写入每条数据
      for (Model model : models) {
        writer.write(model.getModelName() + "\t" +
          model.getModelId() + "\t" +
          model.getInputCost() + "\t" +
          model.getOutputCost() + "\t" +
          model.getContextTokens() + "\t" +
          model.getModeration() + "\n");
      }

      System.out.println("数据已成功写入文件: " + outputFilePath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
