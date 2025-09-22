package com.matchalab.trip_todo_api.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HtmlParserService {

    private final String quotedPrintableIndicator = "quoted-printable";
    private final String base64Indicator = "Content-Transfer-Encoding: base64";

    private Optional<String> extractHtml(String rawText) {
        String startTag = "<Html";
        String endTag = "</Html>";

        int startIndex = rawText.indexOf(startTag);
        int endIndex = rawText.lastIndexOf(endTag);

        if (startIndex != -1 && endIndex != -1) {
            return Optional.of(rawText.substring(startIndex, endIndex + endTag.length()));
        } else {
            return Optional.empty();
        }
    }

    private String decodeMIMEString(String encodedBody, String encoding) {
        try {
            // log.info("[decodeMIMEString] encodedBody:\n" + encodedBody);
            InputStream encodedStream = new ByteArrayInputStream(encodedBody.getBytes(StandardCharsets.ISO_8859_1));
            InputStream decodedStream = MimeUtility.decode(encodedStream, encoding);
            String decodedContent = new String(decodedStream.readAllBytes());
            // log.info("[decodeMIMEString] decodedContent:\n" + decodedContent);
            return decodedContent;
        } catch (MessagingException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return encodedBody;
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return encodedBody;
        }
    }

    private String decodeQuotedPrintableEncoding(String encodedBody) {
        return decodeMIMEString(encodedBody, "quoted-printable");
    }

    private String decodeBase64Encoding(String encodedBody) {
        return decodeMIMEString(encodedBody, "base64");
    }

    public String extractTextAndLink(String rawText) {
        Boolean isQuotedPrintableEncoded = rawText.contains(quotedPrintableIndicator);
        Boolean isBase64Encoded = rawText.contains(base64Indicator);
        String result = extractHtml(rawText).map(html -> {
            if (isQuotedPrintableEncoded) {
                html = decodeQuotedPrintableEncoding(html);
            } else if (isBase64Encoded) {
                html = decodeBase64Encoding(html);
            }
            Document doc = Jsoup.parse(html);

            StringBuilder preprocessedContent = new StringBuilder();

            preprocessedContent.append(doc.body().wholeText().trim());
            preprocessedContent.append("\n\n[Href Links]\n");

            Elements links = doc.select("a[href]");

            for (Element link : links) {
                String href = link.attr("href");
                String text = link.text();
                preprocessedContent.append(text).append(": ").append(href).append("\n");
            }
            return preprocessedContent.toString();
        }).orElse(isQuotedPrintableEncoded ? decodeQuotedPrintableEncoding(rawText)
                : isBase64Encoded ? decodeBase64Encoding(rawText) : rawText);

        result = result
                .replaceAll("\\s*(\\r?\\n)\\s*", "\n")
                // .replaceAll("(\\r?\\n){3,}", "\n\n")
                .replaceAll("[ \\t]+", " ")
                .trim();

        return result;
    }
}
