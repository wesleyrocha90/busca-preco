(ns busca-preco.core
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(def game-list (-> "http://api.steampowered.com/ISteamApps/GetApplist/v0001" http/get :body
                   (json/parse-string true) :applist :apps :app))

(defn find-id-by-name [name]
  (loop [[game & more] game-list]
    (if (= (game :name) name) (game :appid) (if more (recur more)))))

(defn find-price-by-id [id]
  (if-not (nil? id)
    (let [game-data (http/get (str "http://store.steampowered.com/api/appdetails?appids=" id))
          game (get (-> game-data :body (json/parse-string true)) (keyword (str id)))]
      (if (game :success) (-> game :data :price_overview :final)))))

(defn find-price-by-name [name]
  (find-price-by-id (find-id-by-name name)))

(defn -main []
  (println
    (find-price-by-id (find-id-by-name "Assassin's Creed Syndicate"))))