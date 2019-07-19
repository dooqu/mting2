package cn.xylink.mting.speech.data;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import cn.xylink.mting.bean.Article;


/*
1、直接点列表的某一项开始播放=> playIterator = article; select
2、某个条目播放完成，使用moveNext，进行向下搜索，再调用current

*/
public class SpeechList {

    LinkedList<Article> internalList;
    ListIterator<Article> playIterator;
    Article current;

    static SpeechList instance;

    public static SpeechList getInstance() {
        if (instance == null) {
            synchronized (SpeechList.class) {
                if (instance == null) {
                    instance = new SpeechList();
                }
            }
        }
        return instance;
    }


    SpeechList() {
        internalList = new LinkedList<>();
        playIterator = internalList.listIterator();
        current = null;
    }


    public synchronized Article getCurrent() {
        return current;
    }


    /*
    选中一个已经存在的条目，参数为条目的id，
    如果未找到这个条目，返回null
     */
    public synchronized Article select(String articleId) {

        if (articleId == null)
            return null;

        if (current != null && articleId.equals(current.getArticleId())) {
            return current;
        }

        ListIterator<Article> it = internalList.listIterator();
        while (it.hasNext()) {
            Article currArt = it.next();
            if (articleId.equals(currArt.getArticleId())) {
                current = currArt;
                playIterator = it;
                return current;
            }
        }
        return null;
    }


    public synchronized Article selectFirst() {

        if (this.internalList.size() <= 0)
            return null;

        playIterator = this.internalList.listIterator();
        current = playIterator.next();

        return current;
    }


    /*
    选中或者插入一条数
     */
    public synchronized Article pushFrontAndSelect(Article article) {
        if (article == null || article.getArticleId() == null)
            return null;

        pushFront(article);

        playIterator = internalList.listIterator(0);
        current = playIterator.next();

        return current;
    }


    private boolean pushFront(Article article)
    {
        boolean isArticleSelected = false;
        //新建迭代器
        ListIterator<Article> it = internalList.listIterator();
        while (it.hasNext()) {
            Article currArt = it.next();

            if (currArt.getArticleId().equals(article.getArticleId())) {
                //先对比引用，如果引用都一样，那必定是同一个对象，没必要更新对换
                isArticleSelected = (current != null) && current.getArticleId().equals(article.getArticleId());
                it.remove();
                break;
            }
        }

        internalList.addFirst(article);

        if (isArticleSelected) {
            playIterator = internalList.listIterator(0);
            current = playIterator.next();
        }
        return isArticleSelected;
    }


    public synchronized void pushFront(List<Article> list) {
        for(int i = 0, j = list.size(); i < j; i++)
        {
            pushFront(list.get(i));
        }
    }


    public synchronized boolean moveNext() {
        /*
        如果current == null，那么没有基准点，不能向下迭代
         */
        if (current == null) {
            return false;
        }
        /*
        如果当前不为空，在moveNext之前，先在迭代器中将现在的选中元素删除，
        同时设置current = null
         */
        playIterator.remove();
        current = null;

        /*
        优先先向后查找
         */
        if (playIterator.hasNext()) {
            current = playIterator.next();
            return true;
        }

        //如果向后查找失败，重溯到开头，看在指针的上方是否还有新加的元素
        //如果有，设置interator = 0
        else if (playIterator.hasPrevious()) {
            playIterator = internalList.listIterator(0);
            current = playIterator.next();
            return true;
        }

        return false;
    }


    public synchronized boolean hasNext() {
        /*
        如果current == null，有两种可能：
        1、队列没有元素了
        2、队列有元素，但没有设定当前selected的元素，这样即使有元素也不能迭代
         */
        if (current == null) {
            return false;
        }

        /*
        如果current != null，但目前队列中只有一个元素，那该元素就是current
        也就是说说除了当前的current，没有其他元素了，也即无pre和next
        注意，该判断要放在后续下一个语句之前，因为current不为空&&size=1的时候，pre可能为真
         */
        if (current != null && this.internalList.size() == 1) {
            return false;
        }

        /*
        如果向下或者向下有元素那就返回
         */
        return playIterator.hasNext() || playIterator.hasPrevious();
    }


    public synchronized List<Article> getArticleList() {
        return this.internalList;
    }

    /*
     从目前的列表中删除一些指定id的对象
     返回值指示当前被选中播放的条目是否被删除了
     */
    public synchronized boolean removeSome(List<String> artIds) {

        boolean selectArticleIsDeleted = false;

        ListIterator<String> iteratorIds = artIds.listIterator();
        while (iteratorIds.hasNext()) {
            String currIdToDelete = iteratorIds.next();

            ListIterator<Article> iteratorArt = this.internalList.listIterator();
            while (iteratorArt.hasNext()) {
                Article currArt = iteratorArt.next();
                if (currArt.getArticleId().equals(currIdToDelete)) {
                    iteratorArt.remove();
                    if (currArt.getArticleId().equals(current.getArticleId())) {
                        selectArticleIsDeleted = true;
                    }
                    break;
                }
            }
        }

        if (selectArticleIsDeleted) {
            current = null;
            playIterator = this.internalList.listIterator();
        }
        return selectArticleIsDeleted;
    }

    public synchronized boolean removeAll() {

        internalList.clear();
        playIterator = internalList.listIterator();

        boolean isSelectedArticleDeleted = current != null;

        current = null;
        return isSelectedArticleDeleted;
    }

    public synchronized void appendArticles(List<Article> list) {
        internalList.addAll(list);
    }



    public synchronized int size() {
        return this.internalList.size();
    }
}
