# JMD-Android

This Github project is a module containing the Android application of JMD.

### Presentation of JMD

JMD ('J'ai Mon Diplôme' in French, 'I Have My Diploma' in English) is a university project started in "M1 MIAGE" and finished in "M2 MIAGE" by Jordi CHARPENTIER and Yoann VANHOESERLANDE. 

Its purpose is to provide to all students an application to calculate their average marks and simulate their graduation in real time.

Several other features are also available.
Examples:
- For students: export one year as PDF. 
You can see an example here : http://www.jordi-charpentier.com/jmd/Example_Mail_Modif.png
- For administrators: tracking changes from one year, with a mail for each change.
You can see an example here : http://www.jordi-charpentier.com/jmd/Example_PDF.pdf

### Technologies used on JMD

- Webservices : Java (+ JAX-RS library).
- Database : MySQL.
- iOS application : Swift
- Android application : Java

### Content of 'JMD-Android'

This project was created with Eclipse with ADT. 
You can see below some explanations of the architecture :

- JMD
- --src : contains all controllers of the application. All the logic of JMD. Mostly divided in 2 parts : student and admin.
- --res
- ----drawable : contains all images and XML drawable files.
- ----layout : contains all views of the applications.
- ----values
- ------string.xml : contains most of the string of the application.

### Screens

http://www.casimages.com/i/150102101812499414.png

http://www.casimages.com/i/15010210181735775.png

http://www.casimages.com/i/15010210181997266.png

http://www.casimages.com/i/15010210182157789.png

### Example

Here an example of the method I use to receive push notifications send from Google Cloud Messaging :

```java
private static void generateNotification(Context context, String message) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		String title = context.getString(R.string.app_name);
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(R.drawable.ic_launcher, message, when);
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		Intent notificationIntent = new Intent(context, Accueil.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		notification.setLatestEventInfo(context, title, message, PendingIntent.getActivity(context, 0, notificationIntent, 0));
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		notificationManager.notify(0, notification);
	}
