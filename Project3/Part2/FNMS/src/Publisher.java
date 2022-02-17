import java.util.*;

abstract class Publisher {
    private Vector<Subscriber> subscribers_ = new Vector<Subscriber>();

    public void Subscribe(Subscriber subscriber) { subscribers_.add(subscriber); } 
    public void Unsubscribe(Subscriber unsubscriber) { subscribers_.remove(unsubscriber); }
    
    protected void Publish(String context, String name, int data) { for (Subscriber subscriber : subscribers_) subscriber.Update(context, name, data); }
}