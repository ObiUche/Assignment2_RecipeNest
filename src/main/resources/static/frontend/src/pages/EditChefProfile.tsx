import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { PageContainer } from '../components/layout/PageContainer';
import { Input } from '../components/common/Input';
import { Button } from '../components/common/Button';
import { Card } from '../components/common/Card';
import { useAuth } from '../contexts/AuthContext';

export const EditChefProfile: React.FC = () => {
  // Read auth state for the current logged-in user and auth actions.
  // Profile text updates and photo upload are now separate backend actions.
  const { user, logout, updateMyProfile, uploadProfilePhoto } = useAuth();

  // Router navigation is used for cancel, password page, and logout redirect.
  const navigate = useNavigate();

  // Keep local form state aligned with the editable backend text profile fields.
  const [formData, setFormData] = useState({
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    location: user?.location || '',
    bio: user?.bio || '',
    cuisineSpeciality: user?.cuisineSpeciality || ''
  });

  // Keep the chosen image file separate from the text form fields.
  // The backend photo endpoint expects multipart/form-data, not JSON.
  const [selectedPhoto, setSelectedPhoto] = useState<File | null>(null);

  // Show a preview of either the current saved profile photo
  // or the newly selected file before submit.
  const [photoPreview, setPhotoPreview] = useState<string>(user?.profilePhoto || '');

  const handlePhotoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;

    // Save the selected file in component state
    setSelectedPhoto(file);

    // Show a temporary local preview for the new image
    if (file) {
      setPhotoPreview(URL.createObjectURL(file));
      return;
    }

    // Fall back to the currently saved backend photo if no file is selected
    setPhotoPreview(user?.profilePhoto || '');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Stay honest if the required auth context methods are not wired yet.
    if (!updateMyProfile) {
      alert('Profile editing is not wired to the backend yet.');
      return;
    }

    // Save the text-based profile fields first.
    const profileUpdated = await updateMyProfile({
      firstName: formData.firstName,
      lastName: formData.lastName,
      location: formData.location,
      bio: formData.bio,
      cuisineSpeciality: formData.cuisineSpeciality
    });

    if (!profileUpdated) {
      alert('Failed to update profile.');
      return;
    }

    // Upload a new photo only if the user selected one.
    // This uses the dedicated backend photo endpoint.
    if (selectedPhoto) {
      if (!uploadProfilePhoto) {
        alert('Profile text was updated, but photo upload is not wired yet.');
        navigate(-1);
        return;
      }

      const photoUpdated = await uploadProfilePhoto(selectedPhoto);

      if (!photoUpdated) {
        alert('Profile text was updated, but photo upload failed.');
        return;
      }
    }

    alert('Profile updated successfully.');
    navigate(-1);
  };

  return (
    <PageContainer>
      <div style={{ maxWidth: '600px', margin: '0 auto' }}>
        <h1 style={{ fontSize: '32px', marginBottom: '24px' }}>Edit Profile</h1>

        <Card>
          <form onSubmit={handleSubmit} style={{ padding: '24px' }}>
            <Input
              label="First Name"
              value={formData.firstName}
              onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
              required
            />

            <Input
              label="Last Name"
              value={formData.lastName}
              onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
              required
            />

            <div style={{ marginBottom: '24px' }}>
              <label
                style={{
                  display: 'block',
                  marginBottom: '8px',
                  fontWeight: 500
                }}
              >
                Profile Photo
              </label>

              {/* Show the current saved image or the newly selected preview */}
              {photoPreview && (
                <div style={{ marginBottom: '12px' }}>
                  <img
                    src={photoPreview}
                    alt="Profile preview"
                    style={{
                      width: '120px',
                      height: '120px',
                      objectFit: 'cover',
                      borderRadius: '50%',
                      border: '1px solid #E5E7EB'
                    }}
                  />
                </div>
              )}

              {/* Select a new image file for upload */}
              <input
                type="file"
                accept="image/*"
                onChange={handlePhotoChange}
                style={{ display: 'block', width: '100%' }}
              />
            </div>

            <Input
              label="Location"
              value={formData.location}
              onChange={(e) => setFormData({ ...formData, location: e.target.value })}
            />

            <Input
              label="Cuisine Speciality"
              value={formData.cuisineSpeciality}
              onChange={(e) => setFormData({ ...formData, cuisineSpeciality: e.target.value })}
            />

            <div style={{ marginBottom: '24px' }}>
              <label
                style={{
                  display: 'block',
                  marginBottom: '8px',
                  fontWeight: 500
                }}
              >
                Bio
              </label>

              <textarea
                value={formData.bio}
                onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
                rows={5}
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #D1D5DB',
                  borderRadius: '8px',
                  fontSize: '16px',
                  fontFamily: 'inherit',
                  resize: 'vertical'
                }}
              />
            </div>

            <div style={{ display: 'flex', gap: '16px', marginBottom: '24px' }}>
              {/* Submit the edited profile fields and optional photo upload */}
              <Button type="submit">Save Changes</Button>

              {/* Return to the previous screen without changing anything */}
              <Button
                type="button"
                variant="secondary"
                onClick={() => navigate(-1)}
              >
                Cancel
              </Button>
            </div>

            <hr
              style={{
                margin: '24px 0',
                border: 'none',
                borderTop: '1px solid #E5E7EB'
              }}
            />

            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              {/* Password flow already has its own separate page */}
              <Link to="/change-password">
                <Button variant="secondary" fullWidth>
                  Change Password
                </Button>
              </Link>

              {/* Logout clears auth state and returns the user to the homepage */}
              <Button
                type="button"
                variant="secondary"
                fullWidth
                onClick={async () => {
                  await logout();
                  navigate('/');
                }}
              >
                Logout
              </Button>
            </div>
          </form>
        </Card>
      </div>
    </PageContainer>
  );
};