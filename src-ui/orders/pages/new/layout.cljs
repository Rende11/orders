(ns orders.pages.new.layout
  (:require [re-frame.core :as rf]
            [reitit.frontend.easy :as rfe]
            [orders.pages.new.model :as m]))


(defn view []
  (let [users @(rf/subscribe [::m/users])]
    [:div#order-new
     [:h3.order-form-header "Create new order"]
     [:form.order-form {:on-submit #(do
                                      (.preventDefault %)
                                      (rf/dispatch [::m/submit]))
                        :autoComplete "off"}

      [:label.required {:for "title-inp"} "Title"]
      [:input.order-form-field
       {:type "text"
        :required true
        :id "title-inp"
        :value @(rf/subscribe [::m/field-value [:order/title]])
        :on-change #(rf/dispatch [::m/field-update [:order/title] (-> % .-target .-value)])}]

      [:label.required {:for "desc-inp"} "Description"]
      [:textarea.order-form-field
       {:required true
        :rows 5
        :id "desc-inp"
        :on-change #(rf/dispatch [::m/field-update [:order/desc] (-> % .-target .-value)])}]

      [:label.required {:for "author-inp"} "Author"]
      [:select.order-form-field
       {:name "author"
        :id "author-inp"
        :required true
        :value (or @(rf/subscribe [::m/field-value [:order/author :user/id]]) "")
        :on-change #(rf/dispatch [::m/field-update [:order/author :user/id] (-> % .-target .-value)])}
       [:option {:value "" :hidden true} "Please select"]
       (for [{:user/keys [id display]} users]
         [:option {:key id :value id}
          display])]

      [:label.required {:for "performer-inp"} "Performer"]
      [:select.order-form-field
       {:name "performer"
        :id "performer-inp"
        :required true
        :value (or @(rf/subscribe [::m/field-value [:order/performer :user/id]]) "")
        :on-change #(rf/dispatch [::m/field-update [:order/performer :user/id] (-> % .-target .-value)])}
       [:option {:value "" :hidden true} "Please select"]
       (for [{:user/keys [id display]} users]
         [:option {:key id :value id}
          display])]


      [:label.required {:for "date"} "Due date"]
      [:input.order-form-field
       {:type "date"
        :id "date"
        :placeholder "due date"
        :required true
        :value @(rf/subscribe [::m/field-value [:order/due-date]])
        :on-change #(rf/dispatch [::m/field-update [:order/due-date] (-> % .-target .-value)])}]

      [:div.btn-block
       [:a.link-btn.btn.btn-cancel {:href (rfe/href :orders-index)} "Cancel"]
       [:input.btn.btn-save {:type "submit" :value "Save"}]]]]))
