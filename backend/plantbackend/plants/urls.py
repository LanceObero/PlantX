from django.urls import path
from .views import PlantIdentifyView, PlantHistoryListView

urlpatterns = [
    path("identify/", PlantIdentifyView.as_view(), name="identify-plant"),
    path("history/", PlantHistoryListView.as_view(), name="plant-history"),
]

