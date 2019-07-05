package cn.xylink.mting.speech.data;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import cn.xylink.mting.model.Article;


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


    public Article getCurrent() {
        synchronized (this) {
            return current;
        }
    }


    /*
    选中一个已经存在的条目，参数为条目的id，
    如果未找到这个条目，返回null
     */
    public Article select(String articleId) {
        if (articleId == null)
            return null;

        synchronized (this) {
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
        }
        return null;
    }


    public Article selectFirst() {
        synchronized (this) {

            if (this.internalList.size() <= 0)
                return null;

            playIterator = this.internalList.listIterator();
            current = playIterator.next();

            return current;
        }
    }


    /*
    选中或者插入一条数
     */
    public Article frontPush(Article article) {

        if (article == null || article.getArticleId() == null)
            return null;

        synchronized (this) {

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

            if(isArticleSelected)
            {
                playIterator = internalList.listIterator(0);
                current = playIterator.next();
            }

            return current;
        }
    }


    public boolean moveNext() {
        synchronized (this) {
            if (current != null) {
                playIterator.remove();
            }

            if (playIterator.hasNext()) {
                current = playIterator.next();
                return true;
            }
            else if (playIterator.hasPrevious()) {
                current = playIterator.previous();
                return true;
            }
            return false;
        }
    }


    public boolean hasNext()
    {
        synchronized (this) {
            if (current == null) {
                return false;
            }

            if(current != null && this.internalList.size() == 1)
            {
                return false;
            }

            return playIterator.hasNext() || playIterator.hasPrevious();
        }
    }



    public List<Article> getArticleList() {
        synchronized (this) {
            return this.internalList;
        }
    }

    /*
     从目前的列表中删除一些指定id的对象
     返回值指示当前被选中播放的条目是否被删除了
     */
    public boolean removeSome(List<String> artIds) {
        synchronized (this) {
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
    }

    public boolean removeAll() {
        synchronized (this) {
            internalList.clear();
            playIterator = internalList.listIterator();

            boolean isSelectedArticleDeleted = current != null;

            current = null;
            return isSelectedArticleDeleted;
        }
    }

    public void appendArticles(List<Article> list) {
        synchronized (this) {
            internalList.addAll(list);
        }
    }

    public int size()
    {
        synchronized (this)
        {
            return this.internalList.size();
        }
    }
}
