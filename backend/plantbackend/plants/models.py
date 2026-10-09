from django.db import models
from django.conf import settings


class PlantHistory(models.Model):
    user = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name="plant_history"
    )
    common_name = models.CharField(max_length=255)
    scientific_name = models.CharField(max_length=255)
    confidence = models.CharField(max_length=20)
    description = models.TextField(blank=True)
    key_features = models.TextField(blank=True)
    image = models.ImageField(
        upload_to="plant_history/",
        blank=True,
        null=True
    )
    identified_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ["-identified_at"]

    def __str__(self):
        return f"{self.common_name} - {self.identified_at}"
