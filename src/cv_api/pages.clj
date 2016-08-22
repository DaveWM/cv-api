(ns cv-api.pages
  (:require [endophile.core :refer [mp]]
            [endophile.hiccup :refer [to-hiccup]]
            [clj-time.format :as time]
            [clojure.set :refer [rename-keys]]))

(defmacro markdown [text]
  `(-> (mp ~text)
       to-hiccup))

(defmacro link [url]
  [:a {:href url} url])

(def tech-type-map {:language "Language"
                    :FE "Front End"
                    :BE "Back End"
                    :tool "Build Tools"
                    :DataStore "Data Stores"
                    :Testing "Testing Frameworks"
                    :Cloud "Public Clouds"
                    :Misc "Miscellaneous"})

(defn format-date [date]
  (time/unparse (time/formatter "MMMM YYYY") date))

(defn workplace [title from to bullet-points & {:keys [url]}]
  [:p
   [:div [:b title]]
   [:div [:i (str (format-date from) " - " (if to
                                             (format-date to)
                                             "Present"))]]
   (when url
     [:a {:href url} url])
   [:ul
    (map (fn [bp] [:li bp]) bullet-points)]])

(defn cv-hiccup [{:keys [personal summary technologies projects work-history education hobbies]}]
  [:html
   [:head
    [:link {:rel "stylesheet" :href "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"}]]
   [:body
    [:div {:class "container"}
     [:h1 {:class "text-center"} "David Martin – Curriculum Vitae"]
     
     [:h2 "Personal"]
     [:dl
      (let [email (:email personal)
            github (str "https://github.com/" (:github-user personal))
            codewars (str "https://www.codewars.com/users/" (:codewars-user personal))]
        (->> [["Full Name" "David William Martin"]
              ["Date of Birth" "30/07/1990"]
              ["Nationality" "British"]
              ["Address" "3 Gassiot Road, Tooting, London, SW17 8LB"]
              ["Email" [:a {:href (str "mailto:" email)} email]]
              ["Github" (link github)]
              ["Codewars" (link codewars)]
              ["Websites" [:ul
                           [:li (link "https://davewm.github.io")]
                           [:li (link "https://www.phonsole.co.uk")]]]]
             (mapcat
              (fn [[title detail]]
                [[:dt title]
                 [:dd detail]]))))]
     
     [:h2 "Summary"]
     [:p (markdown summary)]

     [:h2 "Technical Skills"]
     [:dl
      (->> (-> (group-by :type technologies)
               (rename-keys tech-type-map))
           sort
           (mapcat (fn [[type techs]]
                     [[:dt type]
                      [:dd
                       [:ul (map (fn [tech] [:li (:name tech)]) (sort-by :name techs))]]])))]

     [:h2 "Hobby Projects"]
     [:p (markdown projects)]

     [:h2 "Employment History"]
     (map (fn [{:keys [title from to url bullet-points]}] (workplace title from to bullet-points :url url)) work-history)

     [:h2 "Education"]
     (map (fn [{:keys [name from to highlights]}] (workplace name from to highlights)) education)

     [:h2 "Interests"]
     [:ul
      (map (fn [{:keys [title description]}] [:li (str title " - " description)]) hobbies)]

     [:p {:class "help-block"} "References available upon request"]]]])
