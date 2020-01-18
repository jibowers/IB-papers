import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.google.gson.Gson;

public class Manager {

	
	private static boolean getImage = true;
	
	protected static boolean hasBeenSet = false;
	
	protected static User c = new User(); 
	protected static String[] interests = {"business", "entertainment", "gaming", "general", "music", "science-and-nature", "sport", "technology"};
	
	protected static Timer timerR = new Timer(c.getAutoRefr() * 60000, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			if (hasBeenSet){
				//System.out.println("Refreshing from timer... ");
				
				refreshHomeWindow();
			}
	    }    
	});
	
	protected static Timer timerP = new Timer(c.getAutoPost() * 60000, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			if (hasBeenSet){	
				System.out.println("Auto Posting... ");
				autoPost();
			}
	    }    
	});
	
	
	// GUI elements needed for refreshHomeWindow call
	protected static JFrame frame = new JFrame("Social Media Manager");
	protected static JPanel panel = new JPanel();
	protected static JScrollPane scroll;

	
	public static void main(String[] args) {
		
		frame.setVisible(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
				saveAndExit();
            }
        });
 
        frame.setSize(new Dimension(1000,500));    
       	
		String r = "";
		try {
			r = retrieveFromFile();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		//System.out.println(r);
		
		if (!r.equals("")){
			System.out.println("building from file");
			c = new Gson().fromJson(r, User.class);
			openSettingsWindow();
			frame.setTitle(c.getName() + "'s Social Media Manager");
		}else{
			c = new User();
			
			//System.out.println("Name Window!");
			JFrame nameWindow = new JFrame("Hello");
			
			nameWindow.setSize(200, 200);
			nameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JPanel namePanel = new JPanel();
			JLabel nameLabel = new JLabel("Enter your name: ");
			JTextField nameField = new JTextField(10);
			JButton go = new JButton("Go (Alt+Enter)");
			nameLabel.setLabelFor(nameField);
			namePanel.add(nameLabel);
			namePanel.add(nameField);
			namePanel.add(go);
			nameWindow.add(namePanel);
			nameWindow.pack();
			nameWindow.setVisible(true);
			
			go.setMnemonic(KeyEvent.VK_ENTER);
			go.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					c .setName(nameField.getText());
					nameWindow.dispose();
					openSettingsWindow();
					frame.setTitle(c.getName() + "'s Social Media Manager");
				}
			});
		}
		
		//System.out.println(hasBeenSet);
		//System.out.println(c.getName());
		while (c.getName() == null || !hasBeenSet){
			// sleep
			frame.setVisible(false);
		}
		
		
		timerP.setRepeats(false);
		

        
        JMenuBar menuBar = new JMenuBar();
        JButton settingsB = new JButton("Settings (Alt+S)");
        settingsB.setMnemonic(KeyEvent.VK_S);
        settingsB.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		openSettingsWindow();
        	}
        });
        

        JButton refr = new JButton("Refresh (Alt+R)");
        refr.setMnemonic(KeyEvent.VK_R);
        refr.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent i){
        		 refreshHomeWindow();
        	}
        });
        
        JButton auto = new JButton("Auto-post (Alt+A)");
        auto.setMnemonic(KeyEvent.VK_A);
        auto.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent i){
        		 autoPost();
        		 System.out.println("Autoposting!");
        		 
        	}
        });
      
        
        menuBar.add(settingsB);
        menuBar.add(refr);
        menuBar.add(auto);
        frame.setJMenuBar(menuBar);
        
        timerR.start();
        timerP.start();
        
		//refreshHomeWindow();
        frame.setVisible(true);
		}
	       
	//Refresh the panel
	public static void refreshHomeWindow(){
		System.out.println("Refreshing...");
		panel.removeAll();
		panel.repaint();

        frame.revalidate();
        frame.setVisible(true);
        
		//System.out.println(c.getSourceIds());
		//System.out.println("Articles:");
		c.refreshNews();
		if (c.getSourceIds().isEmpty()){
	       return; 	
	    }
		
		JPanel part;

		JEditorPane myPane;
		for (Article a: c.myRecentNews){
			part = new JPanel();
			part.setLayout(new FlowLayout(FlowLayout.LEADING));
			myPane = new JEditorPane("String", a.toString());
			myPane.setSize(frame.getWidth()-200, 100);
			ImageIcon icon = null;
			BufferedImage img = null;
			 
			if (getImage){
				String fullUrlPath = a.getImageUrl();
				 System.out.println(fullUrlPath);
				 URL url;
	            try {
	               url = new URL(fullUrlPath);
	               img = ImageIO.read(url);
	            } catch (MalformedURLException e) {
	            	System.out.println("Image unavailable for this article");
	            	// get placeholder image
	            	String path = "imagePlaceholder.png";
	            	try {
	            		img = ImageIO.read(new File(path));
						//System.out.println("got placeohlder image");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	               //e.printStackTrace();
	            } catch (IOException e) {
	               //e.printStackTrace();
	            }
	            
			}
			 
               
           
            if (img != null){
            	icon = new ImageIcon(img.getScaledInstance(50, 50, BufferedImage.SCALE_DEFAULT));
            }
            
			JButton link = new JButton(icon);
			link.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
		            //open link
					
					    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
					    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
					        try {
					            desktop.browse(new URL(a.getUrl()).toURI());
					        } catch (Exception f) {
					            f.printStackTrace();
					        }
					    }
					}  
			});
			JButton poster = new JButton("Post about it");
			poster.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					
					JFrame volPost = new JFrame();
					volPost.setSize(500, 300);
	        		volPost.setVisible(true);
	        		
	        		JCheckBox post2fb = new JCheckBox("Facebook");
	        		JCheckBox post2t = new JCheckBox("Twitter");
	        		JLabel mInstruction = new JLabel("Message (optional, but must be < 140 characters for Twitter post)");
	        		JTextArea messageF = new JTextArea(a.getUrl(), 10, 30);
	        		messageF.setLineWrap(true);
	        		JLabel charCount = new JLabel("Character count: " + (messageF.getText().length()));
	        		messageF.getDocument().addDocumentListener(new DocumentListener() {
	        	        @Override
	        	        public void removeUpdate(DocumentEvent e) {
	        	        	charCount.setText("Character count: " + messageF.getText().length());
	        	        }
	        	        @Override
	        	        public void insertUpdate(DocumentEvent e) {
	        	        	charCount.setText("Character count: " + messageF.getText().length());
	        	        }
	        	        @Override
	        	        public void changedUpdate(DocumentEvent arg0) {
	        	        	charCount.setText("Character count: " + messageF.getText().length());
	        	        }
	        	    });
	        		
	        		JButton volPostB = new JButton("Post");
	        		volPostB.addActionListener(new ActionListener(){
	        			public void actionPerformed(ActionEvent p){

	        				String message = messageF.getText().trim();

	        				System.out.println(message);
	        				
	        				if (post2fb.isSelected()){
	        					if(!c.fbCredIsValid()){
    	        					System.out.println("Invalid facebook credentials");
    	        					configErrorWindow();
    	        				}else{
		        					if (c.postToFb(message)){
		        						a.setPosted();
		        					}
    	        				}
    	        				
	    	        		}
	    	        		if (post2t.isSelected()){
	    	        			//System.out.println("You are posting to twitter");
	    	        			if (message.length() > 140){
	    	        				//error 
	    	        				//System.out.println(message.length() + " characters");
	    	        				JFrame lengthError = new JFrame();
	    	        				lengthError.setSize(200, 200);
	    	        				JPanel lErrorP = new JPanel();
	    	        				JLabel lError = new JLabel("Your message is too long");
	    	        				lErrorP.add(lError);
	    	        				lengthError.add(lErrorP);
	    	        				lengthError.setVisible(true);
	    	        			
	    	        			}
	    	        			if(!c.tCredIsValid()){
    	        					System.out.println("Invalid twitter credentials");
    	        					configErrorWindow();
    	        				}else{
    	        				
		        					//System.out.println("Your credentials are valid");
		        					
		        					if (c.postToTwitter(message)){
		        						a.setPosted();
		        					}
    	        				}
    	        			}
	    	        		if (a.hasBeenPosted()){
	    	        			volPost.dispose();
	    	        		}
	        			}
	        		});
	        		JButton cancel = new JButton("Cancel");
	        		cancel.addActionListener(new ActionListener(){
	        			public void actionPerformed(ActionEvent e){
	        				volPost.dispose();
	        			}
	        		});
	        		
	        		JPanel volPostP = new JPanel();
	        		volPostP.add(post2fb);
	        		volPostP.add(post2t);
	        		volPostP.add(mInstruction);
	        		volPostP.add(messageF);
	        		volPostP.add(charCount);
	        		volPostP.add(volPostB);
	        		volPostP.add(cancel);
	        		volPost.add(volPostP);
		         }  
			});
			part.add(link);
			part.add(myPane);
			part.add(poster);
			panel.add(part);
			panel.add(new JSeparator(SwingConstants.HORIZONTAL));
		}
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
	    scroll = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    frame.setContentPane(scroll);
	    //frame.getContentPane().add(scroll, BorderLayout.CENTER);
	    
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	panel.scrollRectToVisible(panel.getBounds());
            }
        });
		
		panel.revalidate();
	
		frame.revalidate();
		System.out.println("Done refreshing!");
		
		timerR.stop();
		timerR.start();
		
	}

	public static void autoPost(){
		//System.out.println("I am entering the autoPost method");
		TreeSet<Article> articlePool = c.myRecentNews;
		//System.out.println("Article pool:" + articlePool);
		TreeMap<String, Word> wordPool = new TreeMap<String, Word>();
		for (Article a: articlePool){
			for (String s: a.getKeyWords()){
				if (wordPool.containsKey(s)){
					wordPool.get(s).increase();
				}else{
					wordPool.put(s, new Word(s));
				}
			}
		}
		
		// figure out most common words
		int max = Integer.MIN_VALUE;
		for(String s: wordPool.keySet()){
			if (wordPool.get(s).getCount() > max){
				max = wordPool.get(s).getCount();
			}
		}
		
		ArrayList<String> keyWords = new ArrayList<String>();
		//System.out.println();
		//System.out.println("Max times a word is duplicated: " + max);
		for (String s: wordPool.keySet()){
			if (wordPool.get(s).getCount() == max){
				keyWords.add(s);
			}
		}
		//System.out.println(keyWords);
		//System.out.println(keyWords.isEmpty());
		ArrayList<Article> toPost = new ArrayList<Article>();
		
		if (!keyWords.isEmpty()){
			// get articles
			for (String i: keyWords){
				for(Article a: articlePool){
					if (a.getKeyWords().contains(i) && !a.hasBeenPosted()){
						toPost.add(a);					// adds the article to posting list
						//articlePool.remove(a);		// takes article out of available pool
					}
					break;
				}
				if (!toPost.isEmpty())
					articlePool.remove(toPost.get(0));
				
			}
		}
		while (toPost.size() < 2){
			toPost.add(articlePool.first());			// takes most recent article and adds to posting list
			articlePool.remove(articlePool.first());	// takes article out of available pool
		}
		
		//System.out.println(toPost.toString());
		
		JFrame autoPostFrame = new JFrame("Automatic Poster");
		autoPostFrame.setSize(600, 400);
		autoPostFrame.setVisible(true);
		
		JPanel autoPostPanel = new JPanel();
		autoPostPanel.setLayout(new BoxLayout(autoPostPanel, BoxLayout.PAGE_AXIS));
		
		JPanel introPanel = new JPanel(new BorderLayout());
		JLabel intro = new JLabel("<html><p>These topics have been posting in the past 24 hours. "
				+ "These messages will be posted to your prefered social media site(s) in ten minutes unless you click \"Cancel\"."
				+ "You may also modify the messages and click \"Post\".</p></html>");
		introPanel.add(intro, BorderLayout.NORTH);
		introPanel.revalidate();
		autoPostPanel.add(introPanel);
		
		
		JPanel topicXPanel = new JPanel();
		JTextArea topicXField = new JTextArea(toPost.get(0).getUrl(), 15, 20);
		topicXField.setAlignmentX(SwingConstants.LEFT);
		JLabel xCharCount = new JLabel("Character count: " + topicXField.getText().length());
		topicXField.getDocument().addDocumentListener(new DocumentListener() {
	        @Override
	        public void removeUpdate(DocumentEvent e) {
	        	xCharCount.setText("Character count: " + topicXField.getText().length());
	        }
	        @Override
	        public void insertUpdate(DocumentEvent e) {
	        	xCharCount.setText("Character count: " + topicXField.getText().length());
	        }
	        @Override
	        public void changedUpdate(DocumentEvent arg0) {
	        	xCharCount.setText("Character count: " + topicXField.getText().length());
	        }
	    });
		//topicXField.setHorizontalAlignment(SwingConstants.LEFT);
		topicXField.setLineWrap(true);
		topicXField.setWrapStyleWord(true);
		JCheckBox topicXFb = new JCheckBox("Facebook");
		JCheckBox topicXT = new JCheckBox("Twitter");
		topicXPanel.add(topicXField);
		topicXPanel.add(topicXFb);
		topicXPanel.add(topicXT);
		topicXPanel.add(xCharCount);
		autoPostPanel.add(topicXPanel, BorderLayout.EAST);
		
		JPanel topicYPanel = new JPanel();
		JTextArea topicYField = new JTextArea(toPost.get(1).getUrl(), 15, 20);
		topicYField.setLineWrap(true);
		topicYField.setWrapStyleWord(true);		
		JLabel yCharCount = new JLabel("Character count: " + topicYField.getText().length());
		topicYField.getDocument().addDocumentListener(new DocumentListener() {
	        @Override
	        public void removeUpdate(DocumentEvent e) {
	        	yCharCount.setText("Character count: " + topicYField.getText().length());
	        }
	        @Override
	        public void insertUpdate(DocumentEvent e) {
	        	yCharCount.setText("Character count: " + topicYField.getText().length());
	        }
	        @Override
	        public void changedUpdate(DocumentEvent arg0) {
	        	yCharCount.setText("Character count: " + topicYField.getText().length());
	        }
	    });
		topicYField.setAlignmentX(SwingConstants.LEFT);
		JCheckBox topicYFb = new JCheckBox("Facebook");
		JCheckBox topicYT = new JCheckBox("Twitter");
		topicYPanel.add(topicYField);
		topicYPanel.add(topicYFb);
		topicYPanel.add(topicYT);
		topicYPanel.add(yCharCount);
		autoPostPanel.add(topicYPanel, BorderLayout.EAST);
		
		JPanel buttons = new JPanel();
		JButton post = new JButton("Post");
		post.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				// post
				executeAutoPost(topicXPanel, toPost.get(0));
				executeAutoPost(topicYPanel, toPost.get(1));
				autoPostFrame.dispose();
			}
		});
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				// close this window
				autoPostFrame.dispose();
			}
		});
		buttons.add(post);
		buttons.add(cancel);
		autoPostPanel.add(buttons);
		
		autoPostFrame.add(autoPostPanel);
		
		
		if (c.getPlatforms().contains("facebook")){
			topicXFb.setSelected(true);
			topicXFb.setSelected(true);
		}
		if (c.getPlatforms().contains("twitter")){
			topicXT.setSelected(true);
			topicXT.setSelected(true);
		}
		
		Timer waitToPost = new Timer(600000000, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (executeAutoPost(topicXPanel, toPost.get(0)) && executeAutoPost(topicYPanel, toPost.get(1))){
					autoPostFrame.dispose();
			    }
			}
		});
		waitToPost.start();
		
		timerP.stop();
		timerP.start();
		
	}
	
	public static boolean executeAutoPost(JPanel panel, Article a){
		String messageF = ((JTextArea) panel.getComponent(0)).getText();
		JCheckBox post2fb = (JCheckBox) panel.getComponent(1);
		JCheckBox post2t = (JCheckBox) panel.getComponent(2);
		
		String message = messageF.trim();
		System.out.println(message.length() + " characters");
		System.out.println(message);
		
		boolean posted = false;
		
		if (post2fb.isSelected()){
			if(!c.fbCredIsValid()){
				System.out.println("Invalid credentials");
				configErrorWindow();
			}else{
				if (c.postToFb(message)){
					a.setPosted();
					posted = true;
				}
			}
		}
		if (post2t.isSelected()){

			//System.out.println("You are posting to twitter");
			if (message.length() > 140){
				//error 
				//System.out.println(message.length() + " characters");
				JFrame lengthError = new JFrame();
				JPanel lErrorP = new JPanel();
				JLabel lError = new JLabel("Your message is too long");
				lErrorP.add(lError);
				lengthError.add(lErrorP);
				lengthError.setVisible(true);
			
			}else{
				if(!c.tCredIsValid()){
					System.out.println("Invalid credentials");
					configErrorWindow();
				}else{
					//System.out.println("Your credentials are valid");
					
					if (c.postToTwitter(message)){
						a.setPosted();
						posted = true;
					}
					//System.out.println("Posting...");
					//System.out.println(message + "\n" + a.getUrl());
					//System.out.println(a.getUrl());
				}
			}
		}
		return posted;
	}
	
	public static void openSettingsWindow(){
		
		JFrame settings = new JFrame("Settings");
		settings.setSize(500, 500);
		//settings.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
		settings.setVisible(true);
		settings.toFront();
		
        settings.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
            	if(hasBeenSet){
            		settings.dispose();
            	}else{
            		saveAndExit();
            	}
            }
        });
		 
		JPanel allSet = new JPanel();
		allSet.setLayout(new BoxLayout(allSet, BoxLayout.PAGE_AXIS));
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setSize(settings.getWidth(), 100);
		
		//tabbedPane.setS
		JPanel set1 = new JPanel();
		set1.setLayout(new BoxLayout(set1, BoxLayout.PAGE_AXIS));
		//set1.setAlignmentY(Component.TOP_ALIGNMENT);
		//set1.add(Box.createRigidArea(new Dimension(tabbedPane.getWidth()/3, tabbedPane.getHeight())));
		
		tabbedPane.addTab("Interests", set1);
		for (String i: interests){
			JCheckBox cb = new JCheckBox(i);
			//cb.setAlignmentX(Component.TOP_ALIGNMENT);
			//cb.setVerticalAlignment(SwingConstants.TOP);
			set1.add(cb);
			if (c.getTopics().contains(i)){
				cb.setSelected(true);
			}
		}
		set1.setSize(settings.getWidth(), tabbedPane.getHeight());
		
		JPanel set2 = new JPanel();
		set2.setLayout(new BoxLayout(set2, BoxLayout.PAGE_AXIS));
		set2.setSize(tabbedPane.getWidth(), tabbedPane.getHeight());
		
		
		JLabel instruction = new JLabel("Configure social media platforms, checkbox indicates preference");
		JButton configInstructions = new JButton("Instructions");
		configInstructions.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				JFrame configInst = new JFrame();
				JPanel configIP = new JPanel();
				String s = "<p>For Facebook: create an app on https://developers.facebook.com,"
						+ "then go to https://developers.facebook.com/tools/explorer, select your app on the right side,"
						+ "click \"Get Token\", \"Get User Access Token\"."
						+ "Add publish_actions to your permissions."
						+ "Then, you must go to https://developers.facebook.com/tools/debug/accesstoken, "
						+ "click \"Extend Access Token\" (which lasts for two months),"
						+ "and copy and paste it into this field. </p><br>"
						+ "<p>For Twitter: Go to https://apps.twitter.com/ and create an app. "
						+ "Once your app is set up, go to the \"Keys and Access Tokens\" panel "
						+ "and copy and paste the consumer key, consumer secret, "
						+ "and access token and access secret (you may have to generate these) "
						+ "into these fields. "
						+ "Make sure your permissions are set to \"	Read, write, and direct messages\"</p></html>";
				// TODO write out instructions
				JLabel configIM = new JLabel("<html><body style='width: " + "300" + "px'>" + s);
				configIP.add(configIM);
				configInst.add(configIP);
				configInst.setVisible(true);
				configInst.pack();
			}
		});
		
		set2.setAlignmentX(Component.LEFT_ALIGNMENT);
		set2.setLayout(new BoxLayout(set2, BoxLayout.PAGE_AXIS));
		JCheckBox fbcb = new JCheckBox("Facebook");
		//fbcb.setAlignmentX(Component.LEFT_ALIGNMENT);
		JCheckBox tcb = new JCheckBox("Twitter");
		//tcb.setAlignmentX(Component.LEFT_ALIGNMENT);
		instruction.setSize(settings.getWidth(), 1);
		set2.add(instruction);
		set2.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		int fieldW = 10;
		

		JTextField fba = new JTextField("App Token:", fieldW);
		JTextField tu = new JTextField("Username:", fieldW);
		JTextField tp = new JPasswordField("Password:", fieldW);
		JTextField tck = new JTextField("Consumer Key:", fieldW);
		JTextField tcs = new JTextField("Consumer Secret:", fieldW);
		JTextField tat = new JTextField("Access Token:", fieldW);
		JTextField tas = new JTextField("Access Secret:", fieldW);
	  
		
		JTextField[] fields = {fba, tu, tp, tck, tcs, tat, tas};
		ArrayList<JPanel> mediaOptions = new ArrayList<JPanel>();
		
		for (JTextField f: fields){
			JPanel field = new JPanel();
			JLabel a = new JLabel(f.getText());
			a.setLabelFor(f);
			f.setText(" ");
			field.add(a);
			field.add(f);
			mediaOptions.add(field);
			
		}
		
		set2.add(fbcb);
		set2.add(mediaOptions.get(0));
		set2.add(tcb);
		set2.add(mediaOptions.get(1));
		set2.add(mediaOptions.get(2));
		
		
		set2.add(mediaOptions.get(3));
		set2.add(mediaOptions.get(4));
		set2.add(mediaOptions.get(5));
		set2.add(mediaOptions.get(6));

		set2.add(configInstructions);
		
		
		tabbedPane.addTab("Configuration", set2);
		
		
		JPanel set3 = new JPanel();
		set3.setSize(tabbedPane.getWidth(), tabbedPane.getHeight());
		set3.setLayout(new BoxLayout(set3, BoxLayout.Y_AXIS));
		String[] units = {"minutes", "hours", "days"};
		JComboBox<String> unitList1 = new JComboBox<String>(units);
		unitList1.setSelectedItem("minutes");
		JComboBox<String> unitList2 = new JComboBox<String>(units);
		unitList2.setSelectedItem("minutes");

		SpinnerModel freqSpinMod1 =  new SpinnerNumberModel(c.getAutoRefr(), //initial value
                1, //min
                60, //max
                1); 
		SpinnerModel freqSpinMod2 =  new SpinnerNumberModel(c.getAutoPost(), //initial value
                1, //min
                60, //max
                1); 
		JSpinner freqSpin1 = new JSpinner(freqSpinMod1);
		JSpinner freqSpin2 = new JSpinner(freqSpinMod2);
		
		JPanel timeRefr = new JPanel();
		timeRefr.add(new JLabel("Refresh Frequency:"));
		timeRefr.add(unitList1);
		timeRefr.add(freqSpin1);  
		
		JPanel timeAuto = new JPanel();
		timeAuto.add(new JLabel("\nAutomatic Post Frequency:"));
		timeAuto.add(unitList2);
		timeAuto.add(freqSpin2); 
		
		set3.add(timeRefr);
		//set3.add(new JSeparator(SwingConstants.HORIZONTAL));
		set3.add(timeAuto);

		tabbedPane.addTab("Timer Settings", set3);
		
		
		JButton save = new JButton("Save");
		save.setMnemonic(KeyEvent.VK_S);
		save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
    			//save everything
				
				ArrayList<String> topics= new ArrayList<String>();
				//sources.clear();
				ArrayList<String> oldTopics = c.getTopics();
				
				Component[] buttons = Arrays.copyOfRange(set1.getComponents(), 0, interests.length-1);
				for (Component l: buttons){
					if (((AbstractButton) l).isSelected()){
						topics.add(((AbstractButton) l).getText());
					}
				}
				//System.out.println(topics);
				c.setTopics(topics);
				c.setSources();
				if(c.getTopics().isEmpty()){
					if (set1.getComponentCount() <= 8){ // keeps from replicating JLabel every time save button is pressed
						JLabel mustInterest = new JLabel("You must select at least one category of interest");
						mustInterest.setForeground(Color.RED);
						set1.add(mustInterest);
						set1.revalidate();
					}
				}
				
				
				// social media settings
				if(!fba.getText().isEmpty())
					c.setFb(fba.getText().trim());
				
				if(!tu.getText().isEmpty())
					c.setTwitter(tu.getText().trim(), tp.getText().trim(), tck.getText().trim(), tcs.getText().trim(), tat.getText().trim(), tas.getText().trim());
				
				ArrayList<String> prefP = new ArrayList<String>();
				if (tcb.isSelected()){
					prefP.add("twitter");
				}
				if (fbcb.isSelected()){
					prefP.add("facebook");
				}
				
				c.setPrefPlatforms(prefP);
				
				// time settings
				if (unitList1.getSelectedItem().equals("hours")){
					c.setRefr((int)freqSpin1.getValue() * 60);
				} else if(unitList1.getSelectedItem().equals("days")){
					c.setRefr((int)freqSpin2.getValue() * 1440);
				} else{
					c.setRefr((int)freqSpin1.getValue());
				}
				

				if (unitList2.getSelectedItem().equals("hours")){
					c.setAutoPost((int)freqSpin2.getValue() * 60);
				} else if(unitList2.getSelectedItem().equals("days")){
					c.setAutoPost((int)freqSpin2.getValue() * 1440);
				}else{
					c.setAutoPost((int)freqSpin2.getValue());
				}
				timerR.setDelay(c.getAutoRefr());
				timerR.stop();
				timerR.start();
				timerP.setDelay(c.getAutoPost());
				timerP.stop();
				timerP.start();
				
				if (!c.getTopics().isEmpty()){ // keeps settings window from closing if no interests
					settings.dispose();
					
				}
				if (!(oldTopics.containsAll(topics) && topics.containsAll(oldTopics) && hasBeenSet)){  //does not refresh if sources are same
					refreshHomeWindow();
				}
				
				hasBeenSet = true;
			}
		});
		JButton nvm = new JButton("Nevermind");
		nvm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				settings.dispose();
				hasBeenSet = true;
			}
			
		
		});
		
		//tabbedPane.revalidate();
		
		JPanel end = new JPanel();
		end.add(save);
		end.add(nvm);
		allSet.add(tabbedPane);
		tabbedPane.setSize(new Dimension(settings.getWidth(), settings.getHeight()-100));
		allSet.add(new JSeparator(SwingConstants.HORIZONTAL));
		allSet.add(end);
		settings.add(allSet);
		settings.pack();
	}

	public static void configErrorWindow(){
		JFrame configError = new JFrame("Configuration Error");
		JPanel configEP = new JPanel();
		JLabel configEL = new JLabel("Error: Please check your social media settings.");
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				configError.dispose();
			}
		});
		JButton openS = new JButton("Open Settings Window");
		openS.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				configError.dispose();
				openSettingsWindow();
			}
		});
		configEP.add(configEL);
		configEP.add(cancel);
		configEP.add(openS);
		configError.add(configEP);
		configError.revalidate();
		configError.setVisible(true);
		configError.setSize(300, 200);
	}
	
	public static void saveAndExit(){
		File myFile = new File("UserSettings.txt");

		c.myRecentNews.clear();

    	String s = c.toJson();
    	
    	try{
	    	myFile.createNewFile();
	        FileOutputStream fOut = new FileOutputStream(myFile);
	        OutputStreamWriter myOutWriter =new OutputStreamWriter(fOut);
	        myOutWriter.append(s);
	        myOutWriter.close();
	        fOut.close();
    	}catch(IOException e){
    		System.out.println("Unable to save");
    		e.printStackTrace();
    		System.exit(1);
    	}
        
        System.out.println("Saved to file!");
        System.exit(0);
		
	}
	
	public static String retrieveFromFile() throws IOException{
	    File myFile = new File("UserSettings.txt");
	    if (myFile.exists()){
	        FileInputStream fIn = new FileInputStream(myFile);
	        BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
	        String aDataRow = "";
	        String aBuffer = ""; //Holds the text
	        while ((aDataRow = myReader.readLine()) != null) 
	        {
	            aBuffer += aDataRow ;
	        }
	        myReader.close();
	        
	        return aBuffer;
	    }else{
	    	return "";
	    }
	}
}