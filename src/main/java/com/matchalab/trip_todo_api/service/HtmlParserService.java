package com.matchalab.trip_todo_api.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.google.api.client.http.MultipartContent.Part;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
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

    // public String extractDecodedHtmlBody(String rawEmailSource) {

    // Properties props = new Properties();
    // Session session = Session.getDefaultInstance(props, null);

    // try (InputStream is = new ByteArrayInputStream(rawEmailSource.getBytes())) {
    // MimeMessage message = new MimeMessage(session, is);

    // Object content = message.getContent();

    // if (content instanceof jakarta.mail.Multipart) {
    // jakarta.mail.Multipart multipart = (jakarta.mail.Multipart) content;

    // for (int i = 0; i < multipart.getCount(); i++) {
    // Part part = multipart.getBodyPart(i);
    // String contentType = part.getContentType();

    // // text/html 파트를 찾았고, Content-Transfer-Encoding 헤더를 직접 처리합니다.
    // if (contentType.toLowerCase().contains("text/html")) {

    // // 1. **Content-Transfer-Encoding** 헤더를 확인합니다.
    // String[] transferEncodings = part.getHeader("Content-Transfer-Encoding");
    // String encoding = (transferEncodings != null && transferEncodings.length > 0)
    // ? transferEncodings[0] : null;

    // // 2. **Part.getContent()**를 사용하면, MimeMessage가 내부적으로
    // // Content-Transfer-Encoding 헤더에 따라 디코딩을 시도합니다.
    // // (예: Base64로 인코딩된 스트림을 자동으로 디코딩합니다.)

    // // BUT: MimeMessage.getContent()는 때때로 Charset 이슈를 일으키므로,
    // // 안전하게 InputStream을 사용하고 MimeUtility로 처리합니다.

    // return decodePartStream(part, encoding);
    // }
    // }
    // }

    // // 단일 파트 메시지(multipart가 아닌 경우) 처리 로직 추가 가능
    // // ...

    // return "HTML content not found.";

    // } catch (MessagingException e) {
    // throw new RuntimeException("Error processing MIME message structure.", e);
    // } catch (IOException e) {
    // throw new RuntimeException("Error reading email input stream.", e);
    // }
    // }

    private String getCharsetFromContentType(String contentType) {
        try {
            jakarta.mail.internet.ContentType ct = new jakarta.mail.internet.ContentType(contentType);
            String charset = ct.getParameter("charset");
            return charset != null ? charset : "UTF-8";
        } catch (MessagingException e) {
            return "UTF-8";
        }
    }
}
