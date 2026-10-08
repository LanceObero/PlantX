import base64
import json
import requests

from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.permissions import AllowAny
from rest_framework.parsers import MultiPartParser, FormParser


class PlantIdentifyView(APIView):
    permission_classes = [AllowAny]
    parser_classes = [MultiPartParser, FormParser]

    def post(self, request):
        image = request.FILES.get("image")

        if not image:
            return Response(
                {
                    "success": False,
                    "error": "No image was uploaded."
                },
                status=400
            )

        try:
            image_data = image.read()
            image_base64 = base64.b64encode(image_data).decode("utf-8")

            prompt = """
Identify the plant in this image.

Analyze the visible leaves, stem, flowers, fruit, and overall plant structure.

Return ONLY valid JSON using exactly this format:

{
    "common_name": "plant common name",
    "scientific_name": "scientific name",
    "confidence": "High/Medium/Low",
    "description": "short description",
    "key_features": "important visible features"
}

If the image does not contain a plant, return:

{
    "common_name": "Not a plant",
    "scientific_name": "N/A",
    "confidence": "Low",
    "description": "No plant was detected in the image.",
    "key_features": "No plant visible"
}

Do not add markdown or any text outside the JSON.
"""

            ollama_response = requests.post(
                "http://localhost:11434/api/chat",
                json={
                    "model": "qwen2.5vl:7b",
                    "messages": [
                        {
                            "role": "user",
                            "content": prompt,
                            "images": [image_base64]
                        }
                    ],
                    "stream": False
                },
                timeout=120
            )

            if ollama_response.status_code != 200:
                return Response(
                    {
                        "success": False,
                        "error": "Ollama returned an error.",
                        "details": ollama_response.text
                    },
                    status=500
                )

            result = ollama_response.json()
            ai_text = result["message"]["content"].strip()

            try:
                plant_data = json.loads(ai_text)
            except json.JSONDecodeError:
                return Response(
                    {
                        "success": False,
                        "error": "The AI returned an invalid JSON response.",
                        "raw_response": ai_text
                    },
                    status=500
                )

            return Response(
                {
                    "success": True,
                    "message": "Plant identified successfully.",
                    "plant": plant_data,
                    "filename": image.name
                },
                status=200
            )

        except requests.exceptions.ConnectionError:
            return Response(
                {
                    "success": False,
                    "error": "Could not connect to Ollama. Make sure Ollama is running."
                },
                status=503
            )

        except requests.exceptions.Timeout:
            return Response(
                {
                    "success": False,
                    "error": "Ollama took too long to respond."
                },
                status=504
            )

        except Exception as e:
            return Response(
                {
                    "success": False,
                    "error": str(e)
                },
                status=500
            )