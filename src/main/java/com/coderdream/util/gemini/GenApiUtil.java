package com.coderdream.util.gemini;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import swiss.ameri.gemini.api.Content;
import swiss.ameri.gemini.api.GeminiException;
import swiss.ameri.gemini.api.GenAi.SafetyRating;
import swiss.ameri.gemini.api.GenAi.UsageMetadata;
import swiss.ameri.gemini.api.GenerationConfig;
import swiss.ameri.gemini.api.GenerativeModel;
import swiss.ameri.gemini.api.SafetySetting;


public class GenApiUtil  implements AutoCloseable {


  public static GenerateContentRequest convert(GenerativeModel model) {
    List<GenerationContent> generationContents = convertGenerationContents(model);
    return new GenerateContentRequest(
      model.modelName(),
      generationContents,
      model.safetySettings(),
      model.generationConfig(),
      model.systemInstruction().isEmpty() ? null :
        new SystemInstruction(
          model.systemInstruction().stream()
            .map(SystemInstructionPart::new)
            .toList()
        )
    );
  }


  private static List<GenerationContent> convertGenerationContents(GenerativeModel model) {
    return model.contents().stream()
      .map(content -> {
        // change to "switch" over sealed type with jdk 21
        if (content instanceof Content.TextContent textContent) {
          return new GenerationContent(
            textContent.role(),
            List.of(
              new GenerationPart(
                textContent.text(),
                null
              )
            )
          );
        } else if (content instanceof Content.MediaContent imageContent) {
          return new GenerationContent(
            imageContent.role(),
            List.of(
              new GenerationPart(
                null,
                new InlineData(
                  imageContent.media().mimeType(),
                  imageContent.media().mediaBase64()
                )
              )
            )
          );
        } else if (content instanceof Content.TextAndMediaContent textAndImagesContent) {
          return new GenerationContent(
            textAndImagesContent.role(),
            Stream.concat(
              Stream.of(
                new GenerationPart(
                  textAndImagesContent.text(),
                  null
                )
              ),
              textAndImagesContent.media().stream()
                .map(imageData -> new GenerationPart(
                  null,
                  new InlineData(
                    imageData.mimeType(),
                    imageData.mediaBase64()
                  )
                ))
            ).toList()
          );
        } else {
          throw new GeminiException("Unexpected content:\n" + content);
        }
      })
      .toList();
  }



/** ====== */




  public record Model(
    String name,
    String baseModelId,
    String version,
    String displayName,
    String description,
    int inputTokenLimit,
    int outputTokenLimit,
    List<String> supportedGenerationMethods,
    double temperature,
    double topP,
    int topK
  ) {
  }

  public record GenerateContentRequest(
    // for some reason, model is required for countToken, but not for the others.
    // But it seems to be acceptable for the others, so we just add it to all for now
    String model,
    List<GenerationContent> contents,
    List<SafetySetting> safetySettings,
    GenerationConfig generationConfig,
    SystemInstruction systemInstruction
  ) {
  }

  public record GenerateContentResponse(
    UsageMetadata usageMetadata,
    List<ResponseCandidate> candidates
  ) {
  }

  public record ResponseCandidate(
    GenerationContent content,
    String finishReason,
    int index,
    List<SafetyRating> safetyRatings
  ) {
  }

  private record SystemInstruction(
    List<SystemInstructionPart> parts
  ) {
  }

  private record SystemInstructionPart(
    String text
  ) {
  }

  private record GenerationContent(
    String role,
    List<GenerationPart> parts
  ) {
  }

  private record GenerationPart(
    // contains one or the other
    String text,
    InlineData inline_data
  ) {
  }

  private record InlineData(
    String mime_type,
    String data
  ) {
  }

  private record ModelResponse(List<Model> models) {
  }

  private interface ThrowingSupplier<T> {
    T get() throws IOException, InterruptedException;
  }

  /**
   * Closes this resource, relinquishing any underlying resources. This method
   * is invoked automatically on objects managed by the
   * {@code try}-with-resources statement.
   *
   * @throws Exception if this resource cannot be closed
   * @apiNote While this interface method is declared to throw
   * {@code Exception}, implementers are <em>strongly</em> encouraged to declare
   * concrete implementations of the {@code close} method to throw more specific
   * exceptions, or to throw no exception at all if the close operation cannot
   * fail.
   *
   * <p> Cases where the close operation may fail require careful
   * attention by implementers. It is strongly advised to relinquish the
   * underlying resources and to internally <em>mark</em> the resource as
   * closed, prior to throwing the exception. The {@code close} method is
   * unlikely to be invoked more than once and so this ensures that the
   * resources are released in a timely manner. Furthermore it reduces problems
   * that could arise when the resource wraps, or is wrapped, by another
   * resource.
   *
   * <p><em>Implementers of this interface are also strongly advised
   * to not have the {@code close} method throw
   * {@link InterruptedException}.</em>
   * <p>
   * This exception interacts with a thread's interrupted status, and runtime
   * misbehavior is likely to occur if an {@code InterruptedException} is
   * {@linkplain Throwable#addSuppressed suppressed}.
   * <p>
   * More generally, if it would cause problems for an exception to be
   * suppressed, the {@code AutoCloseable.close} method should not throw it.
   *
   * <p>Note that unlike the {@link Closeable#close close}
   * method of {@link Closeable}, this {@code close} method is
   * <em>not</em> required to be idempotent.  In other words, calling this
   * {@code close} method more than once may have some visible side effect,
   * unlike {@code Closeable.close} which is required to have no effect if
   * called more than once.
   * <p>
   * However, implementers of this interface are strongly encouraged to make
   * their {@code close} methods idempotent.
   */
  @Override
  public void close() throws Exception {

  }


}
