package bot;

import authentication.SymBotAuth;
import clients.SymBotClient;
import clients.symphony.api.DatafeedClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import exceptions.SymClientException;
import model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.NoContentException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class MessagesTest {

    private String configFilePath = "/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/config.json";
    private SymConfig config;
    private SymBotAuth botAuth;
    private SymBotClient botClient;

    @Before
    public void oneTimeSetUp() {
        SymConfigLoader configLoader = new SymConfigLoader();
        config = configLoader.loadFromFile(configFilePath);
        botAuth = new SymBotAuth(config);
        botAuth.authenticate();
        botClient = SymBotClient.initBot(config, botAuth);
    }

    @Test
    public void messageCreateTest() {

        OutboundMessage message = new OutboundMessage();
        message.setMessage("test <emoji shortcode=\"thumbsup\"/>");
        File file = new File ("/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/innovatebot-img.png");
        File file2 = new File ("/Users/manuela.caicedo/Documents/symphonyapiclient/src/main/resources/report812269383544218386.xlsx");
        File[] files = {file,file2};
        message.setAttachment(files);
        try {
            InboundMessage inboundMessage = botClient.getMessagesClient().sendMessage(botClient.getStreamsClient().getUserIMStreamId(botClient.getUsersClient().getUserFromEmail("manuela.caicedo@symphony.com",true).getId()),message);
            assertNotNull(inboundMessage.getMessage());
            assertNotNull(inboundMessage.getMessageId());
            MessageStatus messageStatus = botClient.getMessagesClient().getMessageStatus(inboundMessage.getMessageId());
            assertNotNull(messageStatus);
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getMessageTest() {
        try {
            OutboundMessage message = new OutboundMessage();
            message.setMessage("test <mention email=\"manuela.caicedo@symphony.com\"/>");
            try {
                InboundMessage inboundMessage = botClient.getMessagesClient().sendMessage(botClient.getStreamsClient().getUserIMStreamId(botClient.getUsersClient().getUserFromEmail("mike.scannell@symphony.com",true).getId()),message);

            } catch (SymClientException e) {
                e.printStackTrace();
            }
            List<InboundMessage> inboundMessages = botClient.getMessagesClient().getMessagesFromStream(botClient.getStreamsClient().getUserIMStreamId(botClient.getUsersClient().getUserFromEmail("mike.scannell@symphony.com",true).getId()),0,0,100);
            assertNotNull(!inboundMessages.isEmpty());
            assertNotNull(inboundMessages.get(0).getMessageText());
            System.out.println(inboundMessages.get(0).getMessageText());
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchMessageTest() {
        try {
            OutboundMessage message = new OutboundMessage();
            message.setMessage("test <hash tag=\"apisearchtest\"/>");
            try {
                InboundMessage inboundMessage = botClient.getMessagesClient().sendMessage(botClient.getStreamsClient().getUserIMStreamId(botClient.getUsersClient().getUserFromEmail("manuela.caicedo@symphony.com", true).getId()), message);

            } catch (SymClientException e) {
                e.printStackTrace();
            }
            Map<String,String> query = new HashMap<>();
            query.put("hashtag", "apisearchtest");
            query.put("streamType", "im");
            List<InboundMessage> result = botClient.getMessagesClient().messageSearch(query, 0, 0, false);
            Assert.assertTrue(!result.isEmpty());
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void getAttachmentTypesTest(){
//        List<String> types = botClient.getMessagesClient().getSupportedAttachmentTypes();
//        Assert.assertTrue(!types.isEmpty());
//    }

    @Test
    public void importMessageTest(){
        OutboundImportMessageList importMessages = new OutboundImportMessageList();
        OutboundImportMessage message = new OutboundImportMessage();
        message.setIntendedMessageFromUserId(botClient.getBotUserInfo().getId());
        message.setIntendedMessageTimestamp(1530197282);
        message.setMessage("<messageML>import message test</messageML>");
        message.setOriginatingSystemId("api client");
        message.setOriginalMessageId("1");
        importMessages.add(message);
        try {
            message.setStreamId(botClient.getStreamsClient().getUserIMStreamId(botClient.getUsersClient().getUserFromEmail("manuela.caicedo@symphony.com", true).getId()));
        } catch (SymClientException e) {
            e.printStackTrace();
        } catch (NoContentException e) {
            e.printStackTrace();
        }
        InboundImportMessageList result = null;
        try {
            result = botClient.getMessagesClient().importMessages(importMessages);
        } catch (SymClientException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(!result.isEmpty());
    }

    @Test
    public void suppressMessageTest(){
            OutboundMessage message = new OutboundMessage();
            message.setMessage("test <hash tag=\"tobesuppressedt\"/>");
            InboundMessage inboundMessage=null;
            try {
                inboundMessage = botClient.getMessagesClient().sendMessage(botClient.getStreamsClient().getUserIMStreamId(botClient.getUsersClient().getUserFromEmail("manuela.caicedo@symphony.com", true).getId()), message);

            } catch (SymClientException e) {
                e.printStackTrace();
            } catch (NoContentException e) {
                e.printStackTrace();
            }
        SuppressionResult result = null;
        try {
            result = botClient.getMessagesClient().suppressMessage(inboundMessage.getMessageId());
        } catch (SymClientException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(result.isSuppressed());

    }

    }