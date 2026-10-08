from django.urls import path
from .views import PlantIdentifyView

urlpatterns = [
    path("identify/", PlantIdentifyView.as_view(), name="identify-plant"),
]